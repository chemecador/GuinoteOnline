package com.chemecador.guinoteonline.ui.screen.game

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.chemecador.guinoteonline.data.network.response.GameStartResponse
import com.chemecador.guinoteonline.ui.viewmodel.game.GameViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.UUID

@Composable
fun SearchGameScreen(
    viewModel: GameViewModel = hiltViewModel(),
    onGameStart: (GameStartResponse) -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val wifiP2pManager = context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
    val activity = LocalContext.current as ComponentActivity
    val channel = wifiP2pManager.initialize(context, context.mainLooper, null)
    val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    var isWifiDirectEnabled by remember { mutableStateOf(false) }
    var isCreator by remember { mutableStateOf(false) }
    var isConnecting = remember { mutableStateOf(false) }
    var matchInfo = remember { mutableStateOf("") }
    var showDialog = remember { mutableStateOf(false) }


    if (!wifiManager.isWifiEnabled) {
        Timber.e("Wi-Fi está desactivado. Por favor, activa el Wi-Fi.")
        Toast.makeText(context, "Wi-Fi está desactivado. Por favor, activa el Wi-Fi.", Toast.LENGTH_LONG).show()
        return
    }

    // BroadcastReceiver para manejar los eventos de Wi-Fi Direct
    val wifiDirectReceiver = remember {
        object : BroadcastReceiver() {
            @SuppressLint("MissingPermission")
            override fun onReceive(context: Context, intent: Intent?) {
                when (intent?.action) {
                    WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                        if (!hasPermissions(context)) {
                            requestPermissions(activity)
                            return
                        }
                        wifiP2pManager.requestPeers(channel) { peerList ->
                            val deviceList = peerList.deviceList
                            Timber.e("Dispositivos encontrados: ${deviceList.size}")
                            Toast.makeText(context, "Dispositivos encontrados: ${deviceList.size}", Toast.LENGTH_SHORT).show()

                            if (!isCreator && deviceList.isNotEmpty() && !isConnecting.value) {
                                isConnecting.value = true
                                val config = WifiP2pConfig().apply {
                                    deviceAddress = deviceList.first().deviceAddress
                                }
                                wifiP2pManager.connect(channel, config, object : WifiP2pManager.ActionListener {
                                    override fun onSuccess() {
                                        Timber.e("Intentando conectar con ${deviceList.first().deviceName}")
                                    }

                                    override fun onFailure(reason: Int) {
                                        isConnecting.value = false
                                        Timber.e("Fallo al intentar conectar con dispositivo")
                                    }
                                })
                            }
                        }
                    }

                    WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                        Timber.e("Conexión establecida")
                        Toast.makeText(context, "Conexión establecida", Toast.LENGTH_SHORT).show()
                        wifiP2pManager.requestConnectionInfo(channel) { info ->
                            if (!hasPermissions(context)) {
                                requestPermissions(activity)
                                return@requestConnectionInfo
                            }
                            if (info.groupFormed) {
                                wifiP2pManager.stopPeerDiscovery(channel, null)
                                if (isCreator && info.isGroupOwner) {
                                    val matchId = UUID.randomUUID().toString()
                                    GlobalScope.launch { startServerToShareMatchId(matchId, matchInfo, showDialog) }
                                } else if (!isCreator && !info.isGroupOwner) {
                                    info.groupOwnerAddress?.hostAddress?.let { serverAddress ->
                                        GlobalScope.launch {
                                            val matchId = startClientToReceiveMatchId(serverAddress)
                                            matchId?.let {
                                                matchInfo.value = "Partida ID: $matchId \nCreada por: ${info.groupOwnerAddress.hostName}"
                                                showDialog.value = true
                                                joinMatchOnServer(matchId)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun discoverPeers() {
        if (!hasPermissions(context)) {
            requestPermissions(activity)
            return
        }
        wifiP2pManager.stopPeerDiscovery(channel, object : WifiP2pManager.ActionListener {
            @SuppressLint("MissingPermission")
            override fun onSuccess() {
                wifiP2pManager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
                    override fun onSuccess() {
                        Timber.d("Buscando dispositivos...")
                    }

                    override fun onFailure(reason: Int) {
                        Timber.e("Fallo al buscar dispositivos: $reason")
                    }
                })
            }

            override fun onFailure(reason: Int) {
                Timber.e("No se pudo detener la búsqueda previa")
            }
        })
    }

    DisposableEffect(Unit) {
        val intentFilter = IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        }
        context.registerReceiver(wifiDirectReceiver, intentFilter)
        onDispose {
            context.unregisterReceiver(wifiDirectReceiver)
            wifiP2pManager.removeGroup(channel, null)
        }
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = "¡Partida Encontrada!") },
            text = { Text(text = matchInfo.value) },
            confirmButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text("Aceptar")
                }
            }
        )
    }

    // Composición de la UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            isCreator = true
            if (!hasPermissions(context)) {
                requestPermissions(activity)
                return@Button
            }
            createGroupWithRetry(wifiP2pManager, channel)
        }) {
            Text(text = "Crear partida privada")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            isCreator = false
            discoverPeers()
        }) {
            Text(text = "Buscar partida privada")
        }
    }
}


fun hasPermissions(context: Context): Boolean {
    return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.NEARBY_WIFI_DEVICES) == PackageManager.PERMISSION_GRANTED
}

fun requestPermissions(activity: Activity) {
    ActivityCompat.requestPermissions(
        activity,
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.NEARBY_WIFI_DEVICES),
        100
    )
}


@SuppressLint("MissingPermission")
fun createGroupWithRetry(
    wifiP2pManager: WifiP2pManager,
    channel: WifiP2pManager.Channel,
    retryCount: Int = 3
) {
    wifiP2pManager.removeGroup(channel, object : WifiP2pManager.ActionListener {
        override fun onSuccess() {
            Timber.d("Grupo anterior eliminado, intentando crear un nuevo grupo")
            wifiP2pManager.createGroup(channel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Timber.d("Grupo creado exitosamente")
                }

                override fun onFailure(reason: Int) {
                    if (retryCount > 0) {
                        Timber.e("Fallo al crear el grupo, reintentando...")
                        createGroupWithRetry(wifiP2pManager, channel, retryCount - 1)
                    } else {
                        Timber.e("Fallo al crear el grupo después de varios intentos")
                    }
                }
            })
        }

        override fun onFailure(reason: Int) {
            Timber.e("No se pudo eliminar el grupo anterior")
        }
    })
}

suspend fun startClientToReceiveMatchId(serverAddress: String): String? {
    return withContext(Dispatchers.IO) {
        try {
            val socket = Socket(serverAddress, 8888)
            Timber.e("Conectado al servidor para recibir el ID de partida")

            val input = BufferedReader(InputStreamReader(socket.getInputStream()))
            val matchId = input.readLine()  // Recibir el ID de partida

            socket.close()
            matchId
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}

private var serverStarted = false

suspend fun startServerToShareMatchId(
    matchId: String,
    matchInfo: MutableState<String>,
    showDialog: MutableState<Boolean>
) {
    if (serverStarted) {
        Timber.e("Server ya está iniciado en este puerto")
        return
    }

    // Actualizar la UI fuera del bloque de red
    withContext(Dispatchers.Main) {
        matchInfo.value = "Partida ID: $matchId \nCreada por: ${InetAddress.getLocalHost().hostAddress}"
        showDialog.value = true
    }

    // Ejecutar la lógica de red en Dispatchers.IO
    withContext(Dispatchers.IO) {
        var serverSocket: ServerSocket? = null
        try {
            serverSocket = ServerSocket(8888)
            serverStarted = true
            Timber.e("Esperando conexión del buscador...")

            // Aceptar conexión en el hilo de red
            val clientSocket = serverSocket.accept()
            Timber.e("Conectado al buscador")

            val output = PrintWriter(
                BufferedWriter(OutputStreamWriter(clientSocket.getOutputStream())),
                true
            )
            output.println(matchId) // Enviar el ID de partida

            clientSocket.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            serverSocket?.close()
            serverStarted = false
            Timber.e("ServerSocket cerrado")
        }
    }
}



fun joinMatchOnServer(matchId: String?) {
    matchId?.let {
        // Realiza una solicitud a tu servidor usando Retrofit, WebSockets, etc.
        // para unirse a la partida privada utilizando el matchId.
        Timber.e("Uniéndose a la partida con ID: $matchId")
    }
}






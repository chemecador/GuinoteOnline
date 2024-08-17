package com.chemecador.guinoteonline.ui.screen.game.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchGameViewModel @Inject constructor() : ViewModel() {

    private val _gameStatus = MutableLiveData<String>()
    val gameStatus: LiveData<String> get() = _gameStatus

    private val _startGameEvent = MutableLiveData<Pair<String, String>>()
    val startGameEvent: LiveData<Pair<String, String>> get() = _startGameEvent

    private val socket: Socket

    init {
        val opts = IO.Options()
        opts.forceNew = true
        opts.reconnection = true
        socket = IO.socket("http://10.0.2.2:3000", opts)

        socket.on(Socket.EVENT_CONNECT) {
            Timber.tag("Socket").d("Connected")
        }

        socket.on("waiting") { args ->
            if (args.isNotEmpty()) {
                val data = args[0] as JSONObject
                val message = data.optString("message", "Esperando rival...")
                _gameStatus.postValue(message)
            }
        }

        socket.on("game_start") { args ->
            if (args.isNotEmpty()) {
                val data = args[0] as JSONObject
                val userId1 = data.optString("userId1", "Jugador 1")
                val userId2 = data.optString("userId2", "Jugador 2")
                _startGameEvent.postValue(userId1 to userId2)
            }
        }

        socket.on(Socket.EVENT_DISCONNECT) {
            Timber.tag("Socket").d("Disconnected")
        }

        socket.connect()
    }

    fun searchForGame(userId: String) {
        socket.emit("search_game", userId)
    }

    override fun onCleared() {
        super.onCleared()
        socket.disconnect()
    }
}



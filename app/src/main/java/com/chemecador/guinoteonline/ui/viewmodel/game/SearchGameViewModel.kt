package com.chemecador.guinoteonline.ui.viewmodel.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chemecador.guinoteonline.data.model.CardUtils
import com.chemecador.guinoteonline.data.network.response.GameStartResponse
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

    private val _startGameEvent = MutableLiveData<GameStartResponse>()
    val startGameEvent: LiveData<GameStartResponse> get() = _startGameEvent

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


                val gameStartResponse = GameStartResponse(
                    message = data.optString("message"),
                    gameId = data.optString("gameId"),
                    userId1 = data.optString("userId1"),
                    userId2 = data.optString("userId2"),
                    playerCards = data.getJSONArray("playerCards").let { jsonArray ->
                        List(jsonArray.length()) { CardUtils.stringToCard(jsonArray.getString(it)) }
                    },
                    triunfoCard = CardUtils.stringToCard(data.optString("triunfoCard"))
                )
                Timber.tag("GameStart").i(gameStartResponse.toString())

                _startGameEvent.postValue(gameStartResponse)
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

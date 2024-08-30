package com.chemecador.guinoteonline.ui.viewmodel.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chemecador.guinoteonline.data.model.Card
import com.chemecador.guinoteonline.data.model.CardUtils
import com.chemecador.guinoteonline.data.network.response.GameStartResponse
import com.chemecador.guinoteonline.data.repositories.AuthRepository
import com.chemecador.guinoteonline.di.NetworkModule.BASE_URL
import dagger.hilt.android.lifecycle.HiltViewModel
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.launch
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _gameStatus = MutableLiveData<String>()
    val gameStatus: LiveData<String> get() = _gameStatus

    private val _startGameEvent = MutableLiveData<GameStartResponse>()
    val startGameEvent: LiveData<GameStartResponse> get() = _startGameEvent

    private val _currentTurn = MutableLiveData<String>()
    val currentTurn: LiveData<String> get() = _currentTurn

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> get() = _token


    private val socket: Socket

    init {
        val opts = IO.Options()
        opts.forceNew = true
        opts.reconnection = true
        socket = IO.socket(BASE_URL, opts)

        setupSocketListeners()

        socket.connect()
        fetchToken()
    }

    private fun fetchToken() {
        viewModelScope.launch {
            _token.value = authRepository.getAuthToken()
        }
    }

    private fun setupSocketListeners() {
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
                    myUsername = data.optString("myUsername"),
                    opponentUsername = data.optString("opponentUsername"),
                    playerCards = data.getJSONArray("playerCards").let { jsonArray ->
                        List(jsonArray.length()) { CardUtils.stringToCard(jsonArray.getString(it)) }
                    },
                    triunfoCard = CardUtils.stringToCard(data.optString("triunfoCard")),
                    currentTurn = data.optString("currentTurn")
                )
                Timber.tag("GameStart").i(gameStartResponse.toString())
                _startGameEvent.postValue(gameStartResponse)
            }
        }

        socket.on("update_turn") { args ->
            if (args.isNotEmpty()) {
                val data = args[0] as JSONObject
                val newTurn = data.optString("currentTurn")
                _currentTurn.postValue(newTurn)
            }
        }

        socket.on(Socket.EVENT_DISCONNECT) {
            Timber.tag("Socket").d("Disconnected")
        }
    }

    fun searchForGame() {
        socket.emit("search_game", token.value)
    }

    fun playCard(card: Card) {
        socket.emit("play_card", card)
    }

    override fun onCleared() {
        super.onCleared()
        socket.disconnect()
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.clearAuthToken()
        }
    }
}

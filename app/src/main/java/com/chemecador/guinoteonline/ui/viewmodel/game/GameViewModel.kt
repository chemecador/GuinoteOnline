package com.chemecador.guinoteonline.ui.viewmodel.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chemecador.guinoteonline.data.model.Card
import com.chemecador.guinoteonline.data.model.CardUtils
import com.chemecador.guinoteonline.data.model.GameResult
import com.chemecador.guinoteonline.data.network.response.GameStartResponse
import com.chemecador.guinoteonline.data.network.response.Player
import com.chemecador.guinoteonline.data.repositories.AuthRepository
import com.chemecador.guinoteonline.di.NetworkModule.BASE_URL
import dagger.hilt.android.lifecycle.HiltViewModel
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _currentGameId = MutableLiveData<String?>()

    private val _gameStatus = MutableLiveData<String>()
    val gameStatus: LiveData<String> get() = _gameStatus

    private val _toastMessage = MutableLiveData<String?>()
    val toastMessage: LiveData<String?> get() = _toastMessage

    private val _startGameEvent = MutableLiveData<GameStartResponse>()
    val startGameEvent: LiveData<GameStartResponse> get() = _startGameEvent

    private val _currentTurn = MutableLiveData<String>()
    val currentTurn: LiveData<String> get() = _currentTurn

    private val _token = MutableLiveData<String>()

    private val _opponentPlayedCards = MutableLiveData<Card?>()
    val opponentPlayedCards: MutableLiveData<Card?> get() = _opponentPlayedCards

    private val _centerCards = MutableLiveData<Card?>()
    val centerCards: MutableLiveData<Card?> get() = _centerCards

    private val _triunfoCard = MutableLiveData<Card>()
    val triunfoCard: LiveData<Card> get() = _triunfoCard

    private val _playerCards = MutableLiveData<List<Card>>()
    val playerCards: LiveData<List<Card>> get() = _playerCards

    private val _playerWonCards = MutableLiveData<List<Card>>()
    val playerWonCards: LiveData<List<Card>> get() = _playerWonCards

    private val _opponentWonCards = MutableLiveData<List<Card>>()
    val opponentWonCards: LiveData<List<Card>> get() = _opponentWonCards

    private val _isInteractionEnabled = MutableLiveData(true)

    private val _team1Points = MutableLiveData(0)
    val team1Points: LiveData<Int> get() = _team1Points

    private val _team2Points = MutableLiveData(0)
    val team2Points: LiveData<Int> get() = _team2Points

    private val _canCantar = MutableLiveData<Boolean>()
    val canCantar: LiveData<Boolean> get() = _canCantar

    private val _hasExchangedSeven = MutableLiveData(false)

    private val _deUltimas = MutableLiveData(false)
    val deUltimas: LiveData<Boolean> get() = _deUltimas

    private val _lastWinner = MutableLiveData<String>()

    private val _gameResult = MutableLiveData<GameResult>()
    val gameResult: LiveData<GameResult> get() = _gameResult

    private val _isGameEnded = MutableLiveData(false)
    val isGameEnded: LiveData<Boolean> get() = _isGameEnded

    private val palosCantados = mutableSetOf<String>()

    private lateinit var gameStartResponse: GameStartResponse

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

                val players = data.getJSONArray("players").let { jsonArray ->
                    List(jsonArray.length()) { index ->
                        val playerObject = jsonArray.getJSONObject(index)
                        Player(
                            role = playerObject.optString("role"),
                            username = playerObject.optString("username")
                        )
                    }
                }

                val triunfoCard = CardUtils.fromString(data.optString("triunfoCard"))
                _triunfoCard.postValue(triunfoCard)
                gameStartResponse = GameStartResponse(
                    message = data.optString("message"),
                    gameId = data.optString("gameId"),
                    players = players,
                    myRole = data.optString("myRole"),
                    playerCards = data.getJSONArray("playerCards").let { jsonArray ->
                        List(jsonArray.length()) { CardUtils.fromString(jsonArray.getString(it)) }
                    },
                    triunfoCard = triunfoCard,
                    currentTurn = data.optString("currentTurn")
                )

                _playerCards.postValue(gameStartResponse.playerCards)

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

        socket.on("card_played") { args ->
            if (args.isNotEmpty()) {
                val jsonData = JSONObject(args[0].toString())
                val cardName = jsonData.getString("card")
                val card = CardUtils.fromString(cardName)
                val playedBy = jsonData.getString("playedBy")
                val newTurn = jsonData.getString("currentTurn")

                viewModelScope.launch {
                    if (playedBy != gameStartResponse.myRole) {
                        _opponentPlayedCards.postValue(card)
                    }

                    _currentTurn.postValue(newTurn)
                }
            }
        }
        socket.on("new_card") { args ->
            if (args.isNotEmpty()) {
                val jsonData = JSONObject(args[0].toString())
                val cardName = jsonData.getString("newCard")
                val newCard = CardUtils.fromString(cardName)

                viewModelScope.launch {
                    val updatedCards = _playerCards.value?.toMutableList() ?: mutableListOf()
                    updatedCards.add(newCard)
                    _playerCards.postValue(updatedCards)
                    checkIfCanCantar()
                }

                Timber.d("Nueva carta recibida: $newCard")
            }
        }
        socket.on("de_ultimas") { args ->
            if (args.isNotEmpty()) {
                val data = args[0] as JSONObject
                val message = data.getString("message")
                _centerCards.postValue(null)
                _opponentPlayedCards.postValue(null)
                _deUltimas.postValue(true)
                _toastMessage.postValue(message)
            }
        }
        socket.on("round_winner") { args ->
            if (args.isNotEmpty()) {
                val data = args[0] as JSONObject
                val winner = data.getString("winner")
                val pointsGained = data.getInt("pointsGained")
                val team1Points = data.getInt("team1Points")
                val team2Points = data.getInt("team2Points")
                val nextTurn = data.getString("nextTurn")

                val playedCard = _centerCards.value
                val opponentPlayedCard = _opponentPlayedCards.value

                _lastWinner.postValue(winner)
                _isInteractionEnabled.postValue(false)

                viewModelScope.launch {
                    _team1Points.postValue(team1Points)
                    _team2Points.postValue(team2Points)
                    delay(1750) // Give the user some time to see the cards played
                    _centerCards.postValue(null)
                    _opponentPlayedCards.postValue(null)
                    _currentTurn.postValue(nextTurn)

                    if (winner == gameStartResponse.myRole) {
                        val updatedPlayerWonCards =
                            _playerWonCards.value?.toMutableList() ?: mutableListOf()
                        if (playedCard != null) updatedPlayerWonCards.add(playedCard)
                        if (opponentPlayedCard != null) updatedPlayerWonCards.add(opponentPlayedCard)
                        _playerWonCards.postValue(updatedPlayerWonCards)
                    } else {
                        val updatedOpponentWonCards =
                            _opponentWonCards.value?.toMutableList() ?: mutableListOf()
                        if (playedCard != null) updatedOpponentWonCards.add(playedCard)
                        if (opponentPlayedCard != null) updatedOpponentWonCards.add(
                            opponentPlayedCard
                        )
                        _opponentWonCards.postValue(updatedOpponentWonCards)
                    }

                    Timber.d("Cartas jugadas limpiadas y asignadas correctamente al ganador")
                    _isInteractionEnabled.postValue(true)
                }

                Timber.tag("RoundWinner")
                    .d("Ganador: $winner, Próximo turno: $nextTurn")
                Timber.tag("RoundWinner")
                    .d("Ganador: $winner, Puntos ganados: $pointsGained, Equipo 1: $team1Points, Equipo 2: $team2Points")
            }
        }

        socket.on("update_turn") { args ->
            if (args.isNotEmpty()) {
                val data = args[0] as JSONObject
                val newTurn = data.optString("currentTurn")
                _currentTurn.postValue(newTurn)
            }
        }

        socket.on("seven_exchanged") { args ->
            if (args.isNotEmpty()) {
                val data = args[0] as JSONObject
                val newTriunfo = data.getString("newTriunfo")
                val exchangedCardString = data.getString("exchangedCard")

                val newTriunfoCard = CardUtils.fromString(newTriunfo)
                val exchangedCard = CardUtils.fromString(exchangedCardString)

                _triunfoCard.postValue(newTriunfoCard)

                if (data.getString("player") == gameStartResponse.myRole) {
                    val updatedHand = _playerCards.value?.toMutableList() ?: mutableListOf()

                    updatedHand.remove(newTriunfoCard)
                    updatedHand.add(exchangedCard)
                    _playerCards.postValue(updatedHand)
                }
            }
        }


        socket.on(Socket.EVENT_DISCONNECT) {
            Timber.tag("Socket").d("Disconnected")
        }

        socket.on("cantar_notificacion") { args ->
            if (args.isNotEmpty()) {
                val data = JSONObject(args[0].toString())
                val player = data.getString("player")
                val points = data.getInt("points")
                val suit = data.getString("suit")

                Timber.d("$player cantó $points puntos en el palo $suit")

                val team1Points = data.getInt("team1Points")
                val team2Points = data.getInt("team2Points")

                viewModelScope.launch {
                    _team1Points.postValue(team1Points)
                    _team2Points.postValue(team2Points)
                }
            }
        }

        socket.on("error") { args ->
            if (args.isNotEmpty()) {
                val data = args[0] as JSONObject
                val errorMessage = data.optString("message", "Ha ocurrido un error desconocido")

                _toastMessage.postValue(errorMessage)
                Timber.e("Error recibido del servidor: $errorMessage")
            }
        }

        socket.on("game_ended") { args ->
            if (args.isNotEmpty()) {
                val data = args[0] as JSONObject
                val winner = data.getString("winner")
                val team1Points = data.getInt("team1Points")
                val team2Points = data.getInt("team2Points")

                _gameResult.postValue(GameResult(winner, team1Points, team2Points))
                _isGameEnded.postValue(true)
            }
        }
    }

    fun searchForGame() {
        socket.emit("search_game", _token.value)
    }

    fun setGameId(gameId: String) {
        _currentGameId.value = gameId
    }

    fun playCard(card: Card) {
        if (_isInteractionEnabled.value == false) return

        viewModelScope.launch {

            val updatedCards = _playerCards.value?.toMutableList() ?: mutableListOf()
            updatedCards.remove(card)
            _playerCards.value = updatedCards

            _centerCards.value = card

            val token = authRepository.getAuthToken()
            val gameId = _currentGameId.value

            if (gameId != null) {
                val data = JSONObject()
                    .put("token", token)
                    .put("card", card)
                    .put("gameId", gameId)
                    .put("players", JSONArray(gameStartResponse.players.map {
                        JSONObject().put("role", it.role).put("username", it.username)
                    }))
                socket.emit("play_card", data)
            }
        }
    }

    private fun checkIfCanCantar() {
        val playerHand = _playerCards.value ?: return
        if (_lastWinner.value != gameStartResponse.myRole) {
            _canCantar.postValue(false)
            return
        }

        val groupedBySuit = playerHand.groupBy { it.palo }

        for ((suit, cards) in groupedBySuit) {
            if (!palosCantados.contains(suit) &&
                cards.any { it.numero == 12 } && cards.any { it.numero == 10 }
            ) {
                _canCantar.postValue(true)
                return
            }
        }

        _canCantar.postValue(false)
    }

    fun cantar() {
        val playerHand = _playerCards.value ?: return
        val triumphSuit = gameStartResponse.triunfoCard.palo

        val groupedBySuit = playerHand.groupBy { it.palo }

        for ((suit, cards) in groupedBySuit) {
            if (!palosCantados.contains(suit) &&
                cards.any { it.numero == 12 } && cards.any { it.numero == 10 }
            ) {

                val points = if (suit == triumphSuit) 40 else 20

                val data = JSONObject()
                    .put("gameId", _currentGameId.value)
                    .put("points", points)
                    .put("suit", suit)
                    .put("player", gameStartResponse.myRole)

                socket.emit("cantar", data)
                Timber.e("Voy a cantar: $points puntos en $suit, $data")

                palosCantados.add(suit)
                _canCantar.postValue(false)
            }
        }
    }

    fun exchangeSeven() {
        viewModelScope.launch {
            val token = authRepository.getAuthToken()
            val gameId = _currentGameId.value

            if (gameId != null) {
                val data = JSONObject()
                    .put("token", token)
                    .put("gameId", gameId)

                socket.emit("exchange_seven", data)

                _hasExchangedSeven.value = true
            }
        }
    }

    fun canExchangeSeven(): Boolean {
        if (_hasExchangedSeven.value == true) {
            return false
        }
        val hasWonLastRound = _lastWinner.value == gameStartResponse.myRole
        val isCurrentTurn = _currentTurn.value == gameStartResponse.myRole
        val playerHand = _playerCards.value ?: return false
        val hasSevenOfTriunfo =
            playerHand.any { it.numero == 7 && it.palo == gameStartResponse.triunfoCard.palo }

        return isCurrentTurn && hasWonLastRound && hasSevenOfTriunfo
    }


    fun clearMessage() {
        _toastMessage.value = null
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

    fun clearGameResult() {
        _isGameEnded.postValue(false)
    }
}

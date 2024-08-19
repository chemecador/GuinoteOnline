package com.chemecador.guinoteonline.data.model

import com.chemecador.guinoteonline.R

data class Card(val palo: String, val numero: Int, val img: Int)

object CardUtils {

    private val paloPriority = mapOf(
        "oros" to 1,
        "copas" to 2,
        "espadas" to 3,
        "bastos" to 4
    )

    private val valuePriority = mapOf(
        1 to 1,
        3 to 2,
        12 to 3,
        10 to 4,
        11 to 5,
        7 to 6,
        6 to 7,
        5 to 8,
        4 to 9,
        2 to 10
    )

    private val valueMap = mapOf(
        "1" to 11,
        "3" to 10,
        "12" to 4,
        "10" to 3,
        "11" to 2
    )

    private val cardResourceMap = mapOf(
        // Bastos
        "bastos1" to R.drawable.bastos1,
        "bastos2" to R.drawable.bastos2,
        "bastos3" to R.drawable.bastos3,
        "bastos4" to R.drawable.bastos4,
        "bastos5" to R.drawable.bastos5,
        "bastos6" to R.drawable.bastos6,
        "bastos7" to R.drawable.bastos7,
        "bastos10" to R.drawable.bastos10,
        "bastos11" to R.drawable.bastos11,
        "bastos12" to R.drawable.bastos12,

        // Copas
        "copas1" to R.drawable.copas1,
        "copas2" to R.drawable.copas2,
        "copas3" to R.drawable.copas3,
        "copas4" to R.drawable.copas4,
        "copas5" to R.drawable.copas5,
        "copas6" to R.drawable.copas6,
        "copas7" to R.drawable.copas7,
        "copas10" to R.drawable.copas10,
        "copas11" to R.drawable.copas11,
        "copas12" to R.drawable.copas12,

        // Espadas
        "espadas1" to R.drawable.espadas1,
        "espadas2" to R.drawable.espadas2,
        "espadas3" to R.drawable.espadas3,
        "espadas4" to R.drawable.espadas4,
        "espadas5" to R.drawable.espadas5,
        "espadas6" to R.drawable.espadas6,
        "espadas7" to R.drawable.espadas7,
        "espadas10" to R.drawable.espadas10,
        "espadas11" to R.drawable.espadas11,
        "espadas12" to R.drawable.espadas12,

        // Oros
        "oros1" to R.drawable.oros1,
        "oros2" to R.drawable.oros2,
        "oros3" to R.drawable.oros3,
        "oros4" to R.drawable.oros4,
        "oros5" to R.drawable.oros5,
        "oros6" to R.drawable.oros6,
        "oros7" to R.drawable.oros7,
        "oros10" to R.drawable.oros10,
        "oros11" to R.drawable.oros11,
        "oros12" to R.drawable.oros12
    )

    fun sortPlayerCards(cards: List<Card>, triunfoPalo: String): List<Card> {
        return cards.sortedWith(compareBy(
            { if (it.palo == triunfoPalo) 0 else paloPriority[it.palo] ?: 5 },
            { valuePriority[it.numero] ?: 11 }
        ))
    }

    fun getValue(card: String): Int {
        val value = card.filter { it.isDigit() }
        return valueMap[value] ?: 0
    }

    private fun getCardResourceId(cardName: String): Int {
        return cardResourceMap[cardName] ?: R.drawable.back
    }

    fun stringToCard(cardString: String): Card {
        if (cardString.isBlank()) return Card("", 0, 0)
        val palo = cardString.filter { it.isLetter() }
        val numero = cardString.filter { it.isDigit() }.toInt()
        val img = getCardResourceId(cardString)
        return Card(palo, numero, img)
    }
}
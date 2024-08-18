package com.chemecador.guinoteonline.utils.mappers

import com.chemecador.guinoteonline.R

class CardResourceMapper {

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

    fun getCardResourceId(cardName: String): Int {
        return cardResourceMap[cardName] ?: R.drawable.back
    }
}

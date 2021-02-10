package com.example.yanji_oblig2

import android.os.Bundle

data class AlpacaParty(val id: String?, val name: String?,
                       val leader: String?, val img: String?, val color: String?){

}
data class CardInfo(val id: String?, val name: String?,
                       val leader: String?, val img: String?, val color: String?, var count: Int, var pert: Int){

}
data class Data(val parties: MutableList<AlpacaParty>?)
data class Vote(var count: Int, var pert: Int)
data class Votes(val id: Int)
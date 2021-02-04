package com.example.yanji_oblig2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val path = "https://www.uio.no/studier/emner/matnat/ifi/IN2000/v21/obligatoriske-oppgaver/alpakkaland/alpacaparties.json"
        val gson = Gson()
        runBlocking {
            try {
                val info = Fuel.get(path).awaitString()
                val partyList = gson.fromJson(info, Data::class.java )
                val partyAdaper = PartyAdapter(partyList)


                println( "the size is :" + partyList.parties?.javaClass?.name)
                println("the name is : "+partyList.parties?.get(1)?.name)
                println("the name is : "+partyList.parties?.get(2)?.name)
                println("the name is : "+partyList.parties?.get(3)?.name)
            } catch(exception: Exception) {
                println("A network request exception was thrown: ${exception.message}")
            }
        }
    }
}
// result generated from /json

data class Data(val parties: MutableList<AlpacaParty>?)


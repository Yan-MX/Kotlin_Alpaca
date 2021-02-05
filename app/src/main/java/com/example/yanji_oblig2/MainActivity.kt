package com.example.yanji_oblig2


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
                val recView =  findViewById<RecyclerView>(R.id.recycle_view)
                recView.adapter = PartyAdapter(partyList.parties)
                recView.layoutManager = LinearLayoutManager(this@MainActivity)
                recView.setHasFixedSize(true)
            } catch(exception: Exception) {
                println("A network request exception was thrown: ${exception.message}")
            }
        }
    }
}
// result generated from /json

data class Data(val parties: MutableList<AlpacaParty>?)


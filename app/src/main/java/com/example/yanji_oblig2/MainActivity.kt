package com.example.yanji_oblig2


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
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

        //spinner
        val spinner = findViewById<Spinner>(R.id.spinner)
        val spinnerOption = arrayOf<String>(getString(R.string.Overall),
                getString(R.string.District1),
                getString(
                        R.string.District2),
                getString(R.string.District3))
        var optionSelected = getString(R.string.Overall)

        spinner.adapter = ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                spinnerOption)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long,
            ) {
               // optionSelected = spinnerOption.get(position)
                optionSelected= position.toString()
                Log.d("hey",position.toString() )
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        val path = "https://www.uio.no/studier/emner/matnat/ifi/IN2000/v21/obligatoriske-oppgaver/alpakkaland/alpacaparties.json"
        val path1 = "https://www.uio.no/studier/emner/matnat/ifi/IN2000/v21/obligatoriske-oppgaver/alpakkaland/district1.json"
        val gson = Gson()

        val distric1 = mutableListOf<Vote>(
                Vote(0, 0),
                Vote(0, 0), Vote(0, 0),
                Vote(0, 0),
        )

        //get votes
        runBlocking {
            try {

                val info1 = Fuel.get(path1).awaitString()
                val votes = gson.fromJson(info1, Array<Votes>::class.java)
                for (vote in votes) {
                    distric1.get(vote.id - 1).count++
                }
                for (vote in distric1){
                   vote.pert = vote.count*100/votes.size
                }



            } catch (exception: Exception) {
                println("A network request exception was thrown: ${exception.message}")
            }
        }

        //get info about the parties
        runBlocking {
            try {
                val info = Fuel.get(path).awaitString()
                val partyList = gson.fromJson(info, Data::class.java)
                //set up adapter
                val recView = findViewById<RecyclerView>(R.id.recycle_view)
                recView.adapter = PartyAdapter(partyList.parties,optionSelected, distric1)
                recView.layoutManager = LinearLayoutManager(this@MainActivity)
                recView.setHasFixedSize(true)
            } catch (exception: Exception) {
                println("A network request exception was thrown: ${exception.message}")
            }
        }
    }
}

// result generated from /json
data class Data(val parties: MutableList<AlpacaParty>?)
data class Vote(var count: Int, var pert: Int)
data class Votes(val id: Int)


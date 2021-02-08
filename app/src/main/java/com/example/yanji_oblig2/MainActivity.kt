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
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main

class MainActivity : AppCompatActivity() {
    val scope = CoroutineScope(Dispatchers.IO + CoroutineName("MyScope"))
    val path = "https://www.uio.no/studier/emner/matnat/ifi/IN2000/v21/obligatoriske-oppgaver/alpakkaland/alpacaparties.json"
    val path1 = "https://www.uio.no/studier/emner/matnat/ifi/IN2000/v21/obligatoriske-oppgaver/alpakkaland/district1.json"
    val path2 = "https://www.uio.no/studier/emner/matnat/ifi/IN2000/v21/obligatoriske-oppgaver/alpakkaland/district2.json"
    val path3 = "https://www.uio.no/studier/emner/matnat/ifi/IN2000/v21/obligatoriske-oppgaver/alpakkaland/district3.xml"

    val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //spinner
        val spinner = findViewById<Spinner>(R.id.spinner)
        val spinnerOption = arrayOf<String>(getString(R.string.District1), getString(
                R.string.District2), getString(R.string.District3), getString(R.string.Overall))

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
                scope.launch { getVotes(position) }
                Log.d("hey2", "Spinner index: "+position.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

    }

    //get votes
    suspend fun getVotes(index: Int) {
        var url: String = ""
        when (index) {
            0 -> url = path1
            1 -> url = path2
            2 -> url = path3
            else -> Log.d("hey", "else is selected")
        }
        var distric = mutableListOf<Vote>(
                Vote(0, 0),
                Vote(0, 0), Vote(0, 0),
                Vote(0, 0),
        )
        Log.d("heyurl", url)
        try {
            val info1 = Fuel.get(url).awaitString()
            val votes = gson.fromJson(info1, Array<Votes>::class.java)
            for (vote in votes) {
                distric.get(vote.id - 1).count++
            }
            for (vote in distric) {
                vote.pert = vote.count * 100 / votes.size
            }
            scope.launch { getParty(distric) }
        } catch (exception: Exception) {
            println("A network request exception was thrown: ${exception.message}")
        }
    }

    fun setUpAdapter(partyList: Data, district: MutableList<Vote>) {
        val recView = findViewById<RecyclerView>(R.id.recycle_view)
        recView.adapter = PartyAdapter(partyList.parties, district)
        recView.layoutManager = LinearLayoutManager(this@MainActivity)
        recView.setHasFixedSize(true)
        Log.d("hey", "Set adapter ok")
    }

    //get info about the parties
    suspend fun getParty(district: MutableList<Vote>) {
        try {
            val info = Fuel.get(path).awaitString()
            val partyList = gson.fromJson(info, Data::class.java)
            Log.d("hey", "get party list ok")
            withContext(Main) {
                setUpAdapter(partyList, district)
            }
        } catch (exception: Exception) {
            println("A network request exception was thrown: ${exception.message}")
        }
    }
}


// result generated from /json
data class Data(val parties: MutableList<AlpacaParty>?)
data class Vote(var count: Int, var pert: Int)
data class Votes(val id: Int)


package com.example.yanji_oblig2


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import java.io.InputStream
import java.lang.reflect.Modifier
import kotlinx.coroutines.withContext as withContext1


class MainActivity : AppCompatActivity() {
    val scope = CoroutineScope(Dispatchers.IO + CoroutineName("MyScope"))
    val path =
        "https://www.uio.no/studier/emner/matnat/ifi/IN2000/v21/obligatoriske-oppgaver/alpakkaland/alpacaparties.json"
    val path1 =
        "https://www.uio.no/studier/emner/matnat/ifi/IN2000/v21/obligatoriske-oppgaver/alpakkaland/district1.json"
    val path2 =
        "https://www.uio.no/studier/emner/matnat/ifi/IN2000/v21/obligatoriske-oppgaver/alpakkaland/district2.json"
    val path3 =
        "https://www.uio.no/studier/emner/matnat/ifi/IN2000/v21/obligatoriske-oppgaver/alpakkaland/district3.xml"

    val gson = Gson()
    var loading=false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //spinner
        val spinner = findViewById<Spinner>(R.id.spinner)
        val spinnerOption = arrayOf<String>(
            getString(R.string.District1), getString(
                R.string.District2
            ), getString(R.string.District3), getString(R.string.Overall)
        )

        spinner.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            spinnerOption
        )

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                scope.launch { showCards(position) }
                Log.d("hey2", "Spinner index: " + position.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

    }

    //the main function, call get votes, partyinfo, combine
    suspend fun showCards(index: Int) {
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        withContext1(Main) {
            progressBar.visibility= View.VISIBLE
        }
        var url: String = ""
        var district = mutableListOf<Vote>()
        when (index) {
            0 -> {
                url = path1
                district = getJsonVote(url)
            }
            1 -> {
                url = path2
                district = getJsonVote(url)
            }
            2 -> {
                url = path3
                district = getXmlVote(url)
            }
            else -> district = getOverallVote()
        }
        val partyList = getParty()
        val combine = combine(partyList, district)


        withContext1(Main) {
            setUpAdapter(combine)
            progressBar.visibility= View.INVISIBLE
        }
    }

    //get json votes path 1 or path 2
    suspend fun getJsonVote(url: String): MutableList<Vote> {
        var district = mutableListOf<Vote>(
            Vote(0, 0),
            Vote(0, 0), Vote(0, 0),
            Vote(0, 0),
        )
        try {
            val info1 = Fuel.get(url).awaitString()
            val votes = gson.fromJson(info1, Array<Votes>::class.java)
            for (vote in votes) {
                district.get(vote.id - 1).count++
            }
            for (vote in district) {
                vote.pert = vote.count * 100 / votes.size
            }
        } catch (exception: Exception) {
            println("A network request exception was thrown: ${exception.message}")
        }
        Log.d("heyurl", "getjsonVote done")
        return district
    }

    //get xml votes using path 3
    suspend fun getXmlVote(url: String): MutableList<Vote> {
        var district = mutableListOf<Vote>(
            Vote(0, 0),
            Vote(0, 0), Vote(0, 0),
            Vote(0, 0),
        )
        val result = khttp.get(url).text
        val inputStream: InputStream = result.byteInputStream()
        val listofParty = XmlParser().parse(inputStream)
        Log.d("heyXml2", "xml parser ok")
        var sum = 0
        for (party in listofParty) {
            if (party.id >= 1) {
                district.get(party.id - 1).count = party.votes
                sum += party.votes
            } else {
                Log.d("heyXML3", "loop error")
            }
        }
        for (vote in district) {
            vote.pert = vote.count * 100 / sum
        }
        return district
    }

    //get votes from all 3 districts
    suspend fun getOverallVote(): MutableList<Vote> {
        var district = mutableListOf<Vote>(
            Vote(0, 0),
            Vote(0, 0), Vote(0, 0),
            Vote(0, 0),
        )
        val district1 = getJsonVote(path1)
        val district2 = getJsonVote(path2)
        val district3 = getXmlVote(path3)
        var sum = 0
        for (index in 0..3) {
            district.get(index).count =
                district1.get(index).count + district2.get(index).count + district3.get(index).count
            sum += district.get(index).count
        }
        for (vote in district) {
            vote.pert = vote.count * 100 / sum
        }
        Log.d("heyOverallVotes", "getOverallVotes done")
        return district
    }

    //get info about the parties
    suspend fun getParty(): Data {
        var partyList = Data(mutableListOf<AlpacaParty>())
        try {
            val info = Fuel.get(path).awaitString()
            partyList = gson.fromJson(info, Data::class.java)
            Log.d("hey", "get partylist ok")

        } catch (exception: Exception) {
            println("A network request exception was thrown: ${exception.message}")
        }
        return partyList
    }

    //combine vote and partyinfo into one list of objects
    suspend fun combine(partyList: Data, district: MutableList<Vote>): MutableList<CardInfo> {
        val combine = mutableListOf<CardInfo>()
        for (index in 0..3) {
            val party = partyList.parties?.get(index)
            val vote = district.get(index)
            val card = CardInfo(
                party?.id,
                party?.name,
                party?.leader,
                party?.img,
                party?.color,
                vote.count,
                vote.pert
            )
            combine.add(index, card)
        }
        Log.d("heycombine", "Combine results: "+combine.toString())
        return combine
    }

    fun setUpAdapter(combine: MutableList<CardInfo>) {
        //sort cards in ascending order
        combine.sortByDescending { it.count }
        val recView = findViewById<RecyclerView>(R.id.recycle_view)
        recView.adapter = PartyAdapter(combine)
        recView.layoutManager = LinearLayoutManager(this@MainActivity)
        recView.setHasFixedSize(true)
        Log.d("hey", "Set adapter ok")
    }

}



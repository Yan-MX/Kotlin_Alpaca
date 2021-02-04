package com.example.yanji_oblig2

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

public class PartyAdapter(val partyList: Data):
    RecyclerView.Adapter<PartyAdapter.ExampleViewHolder>() {

    class ExampleViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){}

    val paries: MutableList<AlpacaParty>? = partyList.parties


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }


}
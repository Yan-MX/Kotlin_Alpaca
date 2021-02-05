package com.example.yanji_oblig2

import android.graphics.Color
import android.provider.Settings.Global.getString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

public class PartyAdapter(private val parties: MutableList<AlpacaParty>?):
    RecyclerView.Adapter<PartyAdapter.ExampleViewHolder>() {

    class ExampleViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val colorView:View= itemView.findViewById(R.id.view)
        val nameView:TextView = itemView.findViewById<TextView>(R.id.text_view)
        val imageView: CircleImageView = itemView.findViewById(R.id.circle_image)
        val leaderView:TextView = itemView.findViewById<TextView>(R.id.text_view2)
        val voteView:TextView = itemView.findViewById<TextView>(R.id.text_view3)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.element,
                parent, false)
        return ExampleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {
        val currentItem = parties?.get(position)
        if (currentItem != null) {
            holder.colorView.setBackgroundColor(Color.parseColor(currentItem.color))
            holder.nameView.text = currentItem.name
            Glide.with(holder.imageView).load(currentItem.img).into(holder.imageView)
            holder.leaderView.text = ("Leader: ${currentItem.leader}")
            holder.voteView.setText(R.string.vote)
        }
    }

    override fun getItemCount(): Int = parties?.size!!




}
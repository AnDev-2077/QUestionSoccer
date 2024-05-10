package com.devapps.questionsoccer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devapps.questionsoccer.R
import com.devapps.questionsoccer.databinding.ItemStatisticsBinding
import com.devapps.questionsoccer.items.StaticResponse
import com.squareup.picasso.Picasso

class StatisticsAdapter (var responseStatistics: List<StaticResponse>) : RecyclerView.Adapter<StatisticsAdapter.StatisticsViewHolder>(){
    class StatisticsViewHolder(view: View): RecyclerView.ViewHolder(view){
        private val binding = ItemStatisticsBinding.bind(view)
        fun bind(responseStatistics: StaticResponse){
            binding.sLiga.text = responseStatistics.league.name
            binding.sPais.text = responseStatistics.league.country
            binding.sTemporada.text = responseStatistics.league.season.toString()
            binding.sEquipo.text = responseStatistics.team.name
            binding.sPartidos.text = responseStatistics.fixtures.played.total.toString()
            binding.sVictorias.text = responseStatistics.fixtures.wins.total.toString()
            binding.sEmpates.text = responseStatistics.fixtures.draws.total.toString()
            binding.sDerrotas.text = responseStatistics.fixtures.loses.total.toString()
            binding.sGolesfavor.text = responseStatistics.goals.`for`.total.total.toString()
            binding.sGolescontra.text = responseStatistics.goals.against.total.total.toString()
            binding.sMayorR.text = responseStatistics.biggest.streak.loses.toString()
            binding.sMayorV.text = responseStatistics.biggest.wins.away
            binding.sMayorD.text = responseStatistics.biggest.loses.away
            binding.sPenalesA.text = responseStatistics.penalty.scored.total.toString()
            binding.sPenalesF.text = responseStatistics.penalty.missed.total.toString()
            binding.sTargetaA.text = responseStatistics.cards.yellow.`91-105`.percentage.toString()
            binding.sTargetaR.text = responseStatistics.cards.red.`91-105`?.percentage.toString()
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return StatisticsViewHolder(layoutInflater.inflate(R.layout.item_statistics,parent,false))
    }

    override fun getItemCount(): Int {
        return responseStatistics.size
    }

    override fun onBindViewHolder(holder: StatisticsViewHolder, position: Int) {
        val item = responseStatistics[position]
        holder.bind(item)
    }
}
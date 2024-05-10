package com.devapps.questionsoccer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devapps.questionsoccer.R
import com.devapps.questionsoccer.databinding.ItemLeagueBinding
import com.devapps.questionsoccer.items.LeagueResponseItem
import com.squareup.picasso.Picasso


class LeaguesAdapter (var responseLeagues: List<LeagueResponseItem>) : RecyclerView.Adapter<LeaguesAdapter.LeaguesViewHolder>(){
    class LeaguesViewHolder(view: View):RecyclerView.ViewHolder(view){
        private val binding = ItemLeagueBinding.bind(view)
        fun bind(responseLeagues: LeagueResponseItem){
            binding.tvLeagueName.text = responseLeagues.league.name
            binding.tvLeagueType.text = responseLeagues.league.type
            Picasso.get().load(responseLeagues.league.logo).into(binding.ivLeagueLogo)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaguesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return LeaguesViewHolder(layoutInflater.inflate(R.layout.item_league,parent,false))
    }

    override fun getItemCount(): Int {
        return responseLeagues.size
    }

    override fun onBindViewHolder(holder: LeaguesViewHolder, position: Int) {
        val item = responseLeagues[position]
        holder.bind(item)
    }
}
package com.devapps.questionsoccer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devapps.questionsoccer.R
import com.devapps.questionsoccer.databinding.ItemSoccerBinding
import com.devapps.questionsoccer.items.ResponseItem
import com.squareup.picasso.Picasso

class SoccerAdapter (var responseTeamsByLeague: List<ResponseItem>, private val onTeamClick: (ResponseItem) -> Unit) : RecyclerView.Adapter<SoccerAdapter.SoccerViewHolder>(){

    class SoccerViewHolder(view:View):RecyclerView.ViewHolder(view){
        private val binding = ItemSoccerBinding.bind(view)
        fun bind(responseItem: ResponseItem, onTeamClick: (ResponseItem) -> Unit){
            binding.tvTeamName.text = responseItem.team.teamName
            binding.tvTeamCode.text = responseItem.team.teamCode
            binding.tvTeamCountry.text = responseItem.team.teamCountry
            binding.tvTeamFounded.text = responseItem.team.teamFounded
            Picasso.get().load(responseItem.team.teamLogo).into(binding.ivTeamLogo)
            binding.LinearLayoutTeams.setOnClickListener {
                onTeamClick(responseItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoccerViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return SoccerViewHolder(layoutInflater.inflate(R.layout.item_soccer,parent,false))
    }

    override fun getItemCount(): Int {
        return responseTeamsByLeague.size
    }

    override fun onBindViewHolder(holder: SoccerViewHolder, position: Int) {
        val item = responseTeamsByLeague[position]
        holder.bind(item, onTeamClick)
    }
}
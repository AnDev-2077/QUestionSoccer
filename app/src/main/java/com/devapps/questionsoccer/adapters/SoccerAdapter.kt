package com.devapps.questionsoccer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devapps.questionsoccer.R
import com.devapps.questionsoccer.databinding.ItemSoccerBinding
import com.devapps.questionsoccer.items.LeagueResponseItem
import com.devapps.questionsoccer.items.ResponseItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import retrofit2.http.Query

class SoccerAdapter (var responseTeamsByLeague: List<ResponseItem>, private val onTeamClick: (ResponseItem) -> Unit) : RecyclerView.Adapter<SoccerAdapter.SoccerViewHolder>(){
    var filteredTeams: List<ResponseItem> = responseTeamsByLeague
    class SoccerViewHolder(view:View):RecyclerView.ViewHolder(view){
        private val binding = ItemSoccerBinding.bind(view)
        fun bind(responseItem: ResponseItem, onTeamClick: (ResponseItem) -> Unit){
            binding.tvTeamName.text = responseItem.team.teamName
            binding.tvTeamCode.text = responseItem.team.teamCode
            binding.tvTeamCountry.text = responseItem.team.teamCountry
            binding.tvTeamFounded.text = responseItem.team.teamFounded
            if (!responseItem.team.teamLogo.isNullOrEmpty()) {
                Picasso.get().load(responseItem.team.teamLogo).into(binding.ivTeamLogo)
            } else {
                Picasso.get().load(R.drawable.enfrentamiento).into(binding.ivTeamLogo)
            }
            Picasso.get().load(responseItem.team.teamLogo).into(binding.ivTeamLogo)
            binding.LinearLayoutTeams.setOnClickListener {
                onTeamClick(responseItem)
            }
            binding.ivTeamFavorites.setOnClickListener{
                addToFavorites(responseItem)
            }

        }
        private fun addToFavorites(team: ResponseItem) {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val db = FirebaseFirestore.getInstance()
                db.collection("users").document(user.uid).collection("favorites").document(team.team.teamId).set(team)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoccerViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return SoccerViewHolder(layoutInflater.inflate(R.layout.item_soccer,parent,false))
    }

    override fun getItemCount(): Int {
        return filteredTeams.size
    }

    override fun onBindViewHolder(holder: SoccerViewHolder, position: Int) {
        val item = filteredTeams[position]
        holder.bind(item, onTeamClick)
    }

    fun filterTeams(query: String){
        filteredTeams = if (query.isEmpty()){
            responseTeamsByLeague
        }else{
            responseTeamsByLeague.filter { it.team.teamName.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }
}
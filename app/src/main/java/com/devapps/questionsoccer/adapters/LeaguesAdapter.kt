package com.devapps.questionsoccer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devapps.questionsoccer.R
import com.devapps.questionsoccer.databinding.ItemLeagueBinding
import com.devapps.questionsoccer.items.LeagueResponseItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso


class LeaguesAdapter (private var responseLeagues: List<LeagueResponseItem>, private val onLeagueClick: (LeagueResponseItem) -> Unit) : RecyclerView.Adapter<LeaguesAdapter.LeaguesViewHolder>(){
    //Varible para filtrar la lista de List<LeagueResponseItem>
    var filteredLeagues: List<LeagueResponseItem> = responseLeagues
    class LeaguesViewHolder(view: View):RecyclerView.ViewHolder(view){
        private val binding = ItemLeagueBinding.bind(view)
        fun bind(responseLeagues: LeagueResponseItem, onLeagueClick: (LeagueResponseItem) -> Unit){
            binding.tvLeagueName.text = responseLeagues.league.name
            binding.tvLeagueType.text = responseLeagues.league.type
            Picasso.get().load(responseLeagues.league.logo).into(binding.ivLeagueLogo)
            binding.LinearLayoutLeagues.setOnClickListener {
                onLeagueClick(responseLeagues)
            }
            binding.ivFavorites.setOnClickListener {
                addToFavorites(responseLeagues)
            }
        }
        private fun addToFavorites(league: LeagueResponseItem) {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val db = FirebaseFirestore.getInstance()
                db.collection("users").document(user.uid).collection("favorites").document(league.league.id.toString()).set(league)
            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaguesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return LeaguesViewHolder(layoutInflater.inflate(R.layout.item_league,parent,false))
    }

    override fun getItemCount(): Int {
        return filteredLeagues.size
    }

    override fun onBindViewHolder(holder: LeaguesViewHolder, position: Int) {
        val item = filteredLeagues[position]
        holder.bind(item, onLeagueClick)
    }

    fun filter(query: String){
        filteredLeagues = if (query.isEmpty()){
            responseLeagues
        }else{
            responseLeagues.filter { it.league.name.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }
}
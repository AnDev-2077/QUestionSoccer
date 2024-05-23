package com.devapps.questionsoccer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devapps.questionsoccer.R
import com.devapps.questionsoccer.databinding.ItemFixtureBinding
import com.devapps.questionsoccer.items.fixtureResponse
import com.squareup.picasso.Picasso

class FixtureAdapter (var responseFixtureByLeague: List<fixtureResponse>, private val onFixtureClick:(fixtureResponse) -> Unit): RecyclerView.Adapter<FixtureAdapter.FixtureViewHolder>() {

    class FixtureViewHolder(view: View): RecyclerView.ViewHolder(view){
        private val binding = ItemFixtureBinding.bind(view)
        fun bind(responseFixtureByLeague: fixtureResponse, onFixtureClick: (fixtureResponse) -> Unit){
            binding.tvFixtureDate.text = responseFixtureByLeague.fixture.date
            binding.tvTeamNameA.text = responseFixtureByLeague.teams.home.name
            binding.tvTeamNameB.text = responseFixtureByLeague.teams.away.name
            Picasso.get().load(responseFixtureByLeague.teams.home.logo).into(binding.ivTeamLogoA)
            Picasso.get().load(responseFixtureByLeague.teams.away.logo).into(binding.ivTeamLogoB)

            binding.root.setOnClickListener{
                onFixtureClick(responseFixtureByLeague)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FixtureViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return FixtureViewHolder(layoutInflater.inflate(R.layout.item_fixture, parent, false))
    }

    override fun getItemCount(): Int {
        return responseFixtureByLeague.size
    }

    override fun onBindViewHolder(holder: FixtureViewHolder, position: Int) {
        val item = responseFixtureByLeague[position]
        holder.bind(item, onFixtureClick)
    }

}
package com.devapps.questionsoccer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devapps.questionsoccer.R
import com.devapps.questionsoccer.databinding.ItemFixtureBinding
import com.devapps.questionsoccer.items.fixtureResponse
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Locale

class FixtureAdapter (var responseFixtureByLeague: List<fixtureResponse>, private val onFixtureClick:(fixtureResponse) -> Unit): RecyclerView.Adapter<FixtureAdapter.FixtureViewHolder>() {

    class FixtureViewHolder(view: View): RecyclerView.ViewHolder(view){
        private val binding = ItemFixtureBinding.bind(view)
        fun bind(responseFixtureByLeague: fixtureResponse, onFixtureClick: (fixtureResponse) -> Unit){

            val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US)
            val targetFormat = SimpleDateFormat("hh:mm a", Locale("es", "PE"))
            val date = originalFormat.parse(responseFixtureByLeague.fixture.date)
            val formattedDate = targetFormat.format(date)

            binding.tvFixtureDate.text = formattedDate
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
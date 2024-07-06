package com.devapps.questionsoccer.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devapps.questionsoccer.R
import com.devapps.questionsoccer.databinding.ItemStandingBinding
import com.devapps.questionsoccer.items.Standing
import com.squareup.picasso.Picasso


class StandingsAdapter (var responseStandings:List<Standing>):RecyclerView.Adapter<StandingsAdapter.StandingsViewHolder>() {
    class StandingsViewHolder(view: View):RecyclerView.ViewHolder(view){
       private val binding = ItemStandingBinding.bind(view)
        fun bind(responseStandings: Standing){
            binding.tvTeamName.text = responseStandings.team.name
            binding.tvPosition.text = responseStandings.rank.toString()
            binding.tvPoints.text = responseStandings.points.toString()
            binding.tvDiff.text = responseStandings.goalsDiff.toString()

            Picasso.get().load(responseStandings.team.logo).into(binding.ivLogo)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StandingsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return  StandingsViewHolder(layoutInflater.inflate(R.layout.item_standing,parent,false))
    }

    override fun getItemCount(): Int {
        return responseStandings.size
    }

    override fun onBindViewHolder(holder: StandingsViewHolder, position: Int) {
        val item = responseStandings[position]
        holder.bind(item)
    }
}
package com.devapps.questionsoccer.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devapps.questionsoccer.items.itemStandingsResponse

class StandingsAdapter (var responseStandings:List<itemStandingsResponse>):RecyclerView.Adapter<StandingsAdapter.StandingsViewHolder>() {
    class StandingsViewHolder(view: View):RecyclerView.ViewHolder(view){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StandingsViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: StandingsViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}
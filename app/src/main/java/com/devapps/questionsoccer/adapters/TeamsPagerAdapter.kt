package com.devapps.questionsoccer.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.devapps.questionsoccer.team_fragments.FixturesByTeam
import com.devapps.questionsoccer.team_fragments.StatisticsByTeam

class TeamsPagerAdapter (
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,

    private val leagueId: Int,
    private val teamId: Int

): FragmentStateAdapter(fragmentManager, lifecycle){
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FixturesByTeam.newInstance(teamId)
            else -> StatisticsByTeam.newInstance(leagueId, teamId)
        }
    }

}
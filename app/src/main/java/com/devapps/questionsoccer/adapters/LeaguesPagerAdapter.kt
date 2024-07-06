package com.devapps.questionsoccer.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.devapps.questionsoccer.league_fragments.FixturesByLeague
import com.devapps.questionsoccer.league_fragments.StandingsByLeague
import com.devapps.questionsoccer.league_fragments.TeamsByLeague

class LeaguesPagerAdapter (
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val leagueId: Int
) : FragmentStateAdapter(fragmentManager, lifecycle){
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TeamsByLeague.newInstance(leagueId)
            1 -> FixturesByLeague.newInstance(leagueId)
            else -> StandingsByLeague.newInstance(leagueId)
        }
    }


}
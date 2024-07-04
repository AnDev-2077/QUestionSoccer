package com.devapps.questionsoccer.league_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.devapps.questionsoccer.R
import com.devapps.questionsoccer.databinding.FragmentStandingsByLeagueBinding
import com.devapps.questionsoccer.items.itemStandingsResponse

class StandingsByLeague : Fragment() {

    private lateinit var binding: FragmentStandingsByLeagueBinding
    private lateinit var adapter: StandingAdapter
    private var StandingsByLeague = mutableListOf<itemStandingsResponse>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = StandingAdapter
        return inflater.inflate(R.layout.fragment_standings_by_league, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance(leagueId: Int) =
            StandingsByLeague().apply {
                arguments = Bundle().apply {
                    putInt("leagueId", leagueId)
                }
            }
    }
}
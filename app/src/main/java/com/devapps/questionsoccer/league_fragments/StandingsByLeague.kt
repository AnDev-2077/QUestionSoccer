package com.devapps.questionsoccer.league_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.devapps.questionsoccer.R

class StandingsByLeague : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

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
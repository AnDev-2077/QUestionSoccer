package com.devapps.questionsoccer.subfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.devapps.questionsoccer.databinding.FragmentLineupBinding
import com.devapps.questionsoccer.items.Coach
import com.devapps.questionsoccer.items.LineupTeam
import com.devapps.questionsoccer.items.Player

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Lineup : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentLineupBinding
    private var teamHome: LineupTeam? = null
    private var teamAway: LineupTeam? = null
    private var lineUpHome: ArrayList<Player>? = null
    private var lineUpAway: ArrayList<Player>? = null
    private var coachHome: Coach? = null
    private var coachAway: Coach? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            teamHome = it.getParcelable("teamHome")
            teamAway = it.getParcelable("teamAway")
            lineUpHome = it.getParcelableArrayList("lineUpHome")
            lineUpAway = it.getParcelableArrayList("lineUpAway")
            coachHome = it.getParcelable("coachHome")
            coachAway = it.getParcelable("coachAway")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLineupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inflateTables()
    }
    private fun inflateTables(){
        binding.tvHomeTeamName.text = teamHome?.name
        binding.tvAwayTeamName.text = teamAway?.name
        binding.tvHomeCoach.text = coachHome?.name
        binding.tvAwayCoach.text = coachAway?.name

        lineUpHome?.let {homePlayers->
            for (i in homePlayers.indices){
                val playerDetails = homePlayers[i].player
                when (i) {
                    0 -> {
                        binding.tvHomePlayerNumber1.text = playerDetails.number.toString()
                        binding.tvHomePlayerName1.text = playerDetails.name
                    }
                    1 -> {
                        binding.tvHomePlayerNumber2.text = playerDetails.number.toString()
                        binding.tvHomePlayerName2.text = playerDetails.name
                    }
                    2 -> {
                        binding.tvHomePlayerNumber3.text = playerDetails.number.toString()
                        binding.tvHomePlayerName3.text = playerDetails.name
                    }
                    3 -> {
                        binding.tvHomePlayerNumber4.text = playerDetails.number.toString()
                        binding.tvHomePlayerName4.text = playerDetails.name
                    }
                    4 -> {
                        binding.tvHomePlayerNumber5.text = playerDetails.number.toString()
                        binding.tvHomePlayerName5.text = playerDetails.name
                    }
                    5 -> {
                        binding.tvHomePlayerNumber6.text = playerDetails.number.toString()
                        binding.tvHomePlayerName6.text = playerDetails.name
                    }
                    6 -> {
                        binding.tvHomePlayerNumber7.text = playerDetails.number.toString()
                        binding.tvHomePlayerName7.text = playerDetails.name
                    }
                    7 -> {
                        binding.tvHomePlayerNumber8.text = playerDetails.number.toString()
                        binding.tvHomePlayerName8.text = playerDetails.name
                    }
                    8 -> {
                        binding.tvHomePlayerNumber9.text = playerDetails.number.toString()
                        binding.tvHomePlayerName9.text = playerDetails.name
                    }
                    9 -> {
                        binding.tvHomePlayerNumber10.text = playerDetails.number.toString()
                        binding.tvHomePlayerName10.text = playerDetails.name
                    }
                    10 -> {
                        binding.tvHomePlayerNumber11.text = playerDetails.number.toString()
                        binding.tvHomePlayerName11.text = playerDetails.name
                    }
                }

            }
        }

        lineUpAway?.let {awayPlayers->
            for (i in awayPlayers.indices){
                val playerDetails = awayPlayers[i].player
                when(i){
                    0 -> {
                        binding.tvAwayPlayerNumber1.text = playerDetails.number.toString()
                        binding.tvAwayPlayerName1.text = playerDetails.name
                    }
                    1 -> {
                        binding.tvAwayPlayerNumber2.text = playerDetails.number.toString()
                        binding.tvAwayPlayerName2.text = playerDetails.name
                    }
                    2 -> {
                        binding.tvAwayPlayerNumber3.text = playerDetails.number.toString()
                        binding.tvAwayPlayerName3.text = playerDetails.name
                    }
                    3 -> {
                        binding.tvAwayPlayerNumber4.text = playerDetails.number.toString()
                        binding.tvAwayPlayerName4.text = playerDetails.name
                    }
                    4 -> {
                        binding.tvAwayPlayerNumber5.text = playerDetails.number.toString()
                        binding.tvAwayPlayerName5.text = playerDetails.name
                    }
                    5 -> {
                        binding.tvAwayPlayerNumber6.text = playerDetails.number.toString()
                        binding.tvAwayPlayerName6.text = playerDetails.name
                    }
                    6 -> {
                        binding.tvAwayPlayerNumber7.text = playerDetails.number.toString()
                        binding.tvAwayPlayerName7.text = playerDetails.name
                    }
                    7 -> {
                        binding.tvAwayPlayerNumber8.text = playerDetails.number.toString()
                        binding.tvAwayPlayerName8.text = playerDetails.name
                    }
                    8 -> {
                        binding.tvAwayPlayerNumber9.text = playerDetails.number.toString()
                        binding.tvAwayPlayerName9.text = playerDetails.name
                    }
                    9 -> {
                        binding.tvAwayPlayerNumber10.text = playerDetails.number.toString()
                        binding.tvAwayPlayerName10.text = playerDetails.name
                    }
                    10 -> {
                        binding.tvAwayPlayerNumber11.text = playerDetails.number.toString()
                        binding.tvAwayPlayerName11.text = playerDetails.name
                    }
                }
            }
        }

    }
    companion object {
        @JvmStatic
        fun newInstance(teamHome: LineupTeam,
                        teamAway: LineupTeam,
                        lineUpHome: List<Player>,
                        lineUpAway: List<Player>,
                        coachHome: Coach,
                        coachAway: Coach
        ) = Lineup().apply {
                arguments = Bundle().apply {
                    putParcelable("teamHome", teamHome)
                    putParcelable("teamAway", teamAway)
                    putParcelableArrayList("lineUpHome", ArrayList(lineUpHome))
                    putParcelableArrayList("lineUpAway", ArrayList(lineUpAway))
                    putParcelable("coachHome", coachHome)
                    putParcelable("coachAway", coachAway)
                }
            }
    }
}
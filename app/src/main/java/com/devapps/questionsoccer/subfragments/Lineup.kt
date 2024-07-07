package com.devapps.questionsoccer.subfragments

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.devapps.questionsoccer.R
import com.devapps.questionsoccer.databinding.FragmentLineupBinding
import com.devapps.questionsoccer.items.Coach
import com.devapps.questionsoccer.items.LineupTeam
import com.devapps.questionsoccer.items.Player
import com.squareup.picasso.Picasso


class Lineup : Fragment() {


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
        addPlayersToGridLayout(lineUpHome, binding.gridLayoutHome)
        addPlayersToGridLayout(lineUpAway, binding.gridLayoutAway)
    }

    private fun addPlayersToGridLayout(players: ArrayList<Player>?, gridLayout: GridLayout) {
        players?.let { playerList ->
            val rowGroups = mutableMapOf<Int, LinearLayout>()

            for (player in playerList) {
                val view = LayoutInflater.from(context).inflate(R.layout.item_lineup, gridLayout, false)
                val playerName: TextView = view.findViewById(R.id.tvPlayerName)
                val playerNumber: TextView = view.findViewById(R.id.tvPlayerNumber)

                playerName.text = player.player.name
                playerNumber.text = player.player.number.toString()

                val gridPosition = player.player.grid?.split(":")
                if (gridPosition != null && gridPosition.size == 2) {
                    val row = gridPosition[0].toIntOrNull()?.minus(1) ?: 0
                    val col = gridPosition[1].toIntOrNull()?.minus(1) ?: 0

                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.CENTER
                    }

                    val rowLayout = rowGroups.getOrPut(row) {
                        LinearLayout(context).apply {
                            orientation = LinearLayout.HORIZONTAL
                            layoutParams = GridLayout.LayoutParams().apply {
                                rowSpec = GridLayout.spec(row, GridLayout.FILL, 1f)
                                columnSpec = GridLayout.spec(0, 5, GridLayout.FILL, 1f)
                                width = GridLayout.LayoutParams.MATCH_PARENT
                                height = GridLayout.LayoutParams.WRAP_CONTENT
                                setGravity(android.view.Gravity.CENTER)
                            }
                            gravity = Gravity.CENTER
                        }.also {
                            gridLayout.addView(it)
                        }
                    }

                    rowLayout.addView(view, params)
                }
            }
        }
    }
    /*private fun inflateTables(){
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

    }*/
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
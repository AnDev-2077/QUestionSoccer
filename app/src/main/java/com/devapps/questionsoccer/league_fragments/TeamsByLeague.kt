package com.devapps.questionsoccer.league_fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import com.devapps.questionsoccer.TeamsDetailsActivity
import com.devapps.questionsoccer.adapters.SoccerAdapter
import com.devapps.questionsoccer.databinding.FragmentTeamsByLeagueBinding
import com.devapps.questionsoccer.interfaces.SoccerService
import com.devapps.questionsoccer.items.ResponseItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class TeamsByLeague : Fragment() {

    private var leagueId: Int? = null

    private lateinit var binding: FragmentTeamsByLeagueBinding
    lateinit var adapter: SoccerAdapter
    private lateinit var recyclerView: RecyclerView
    private var TeamsFragmentResponse = mutableListOf<ResponseItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            leagueId = it.getInt("leagueId")
            Log.d("TeamsByLeague", "Received leagueId in onCreate: $leagueId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTeamsByLeagueBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("TeamsByLeague", "Received leagueId in onViewCreated: $leagueId")
        adapter = SoccerAdapter(TeamsFragmentResponse){ onTeamClick ->
            Log.d("TeamsByLeague", "Clicked on team with teamId: ${onTeamClick.team.teamId}")
            val intent = Intent(activity, TeamsDetailsActivity::class.java)

            intent.putExtra("leagueId", leagueId ?: 0)
            intent.putExtra("teamId", onTeamClick.team.teamId.toInt()) //super importante pasar a to int
            intent.putExtra("teamLogo", onTeamClick.team.teamLogo)
            intent.putExtra("teamName", onTeamClick.team.teamName)
            intent.putExtra("teamCode", onTeamClick.team.teamCode)
            intent.putExtra("teamCountry", onTeamClick.team.teamCountry)
            intent.putExtra("venueName", onTeamClick.venue.venueName)
            intent.putExtra("venueAddress", onTeamClick.venue.venueAddress)
            intent.putExtra("venueCity", onTeamClick.venue.venueCity)
            intent.putExtra("venueCapacity", onTeamClick.venue.venueCapacity)
            intent.putExtra("venueImage", onTeamClick.venue.venueImage)

            Log.d("TeamsByLeague", "Sending leagueId and teamId: ${leagueId ?: 0}, ${onTeamClick.team.teamId}")

            startActivity(intent)
        }
        binding.rvTeamsFragment.layoutManager = LinearLayoutManager(context)
        binding.rvTeamsFragment.adapter = adapter
        getTeams(leagueId ?: 0)
    }

    private fun getRetrofit(): Retrofit {

        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val client = OkHttpClient.Builder().addInterceptor(logging).build()

        return Retrofit.Builder()
            .baseUrl("https://v3.football.api-sports.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    private fun getTeams(leagueId: Int){
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(SoccerService::class.java).getTeamsByLeague(leagueId, 2023)
            val teamsByLeagueResponse = call.body()
            if(call.isSuccessful){
                val teams = teamsByLeagueResponse?.response ?: emptyList()
                withContext(Dispatchers.Main){
                    TeamsFragmentResponse.clear()
                    TeamsFragmentResponse.addAll(teams)
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(leagueId: Int) =
            TeamsByLeague().apply {
                arguments = Bundle().apply {
                    putInt("leagueId", leagueId)
                }
            }
    }
}
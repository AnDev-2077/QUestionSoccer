package com.devapps.questionsoccer

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devapps.questionsoccer.adapters.LeaguesAdapter
import com.devapps.questionsoccer.adapters.SoccerAdapter
import com.devapps.questionsoccer.databinding.FragmentTeamsBinding
import com.devapps.questionsoccer.interfaces.SoccerService
import com.devapps.questionsoccer.items.ResponseItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class Teams : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentTeamsBinding
    private lateinit var adapter: SoccerAdapter
    private lateinit var recyclerView: RecyclerView
    private var TeamsFragmentResponse = mutableListOf<ResponseItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTeamsBinding.inflate(inflater, container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = SoccerAdapter(TeamsFragmentResponse){ onTeamClick ->
            val intent = Intent(activity, TeamsDetailsActivity::class.java)
            intent.putExtra("teamLogo", onTeamClick.team.teamLogo)
            intent.putExtra("teamName", onTeamClick.team.teamName)
            intent.putExtra("teamCode", onTeamClick.team.teamCode)
            intent.putExtra("teamCountry", onTeamClick.team.teamCountry)
            intent.putExtra("venueName", onTeamClick.venue.venueName)
            intent.putExtra("venueAddress", onTeamClick.venue.venueAddress)
            intent.putExtra("venueCity", onTeamClick.venue.venueCity)
            intent.putExtra("venueCapacity", onTeamClick.venue.venueCapacity)
            intent.putExtra("venueImage", onTeamClick.venue.venueImage)
            startActivity(intent)
        }
        binding.rvTeamsFragment.layoutManager = LinearLayoutManager(context)
        binding.rvTeamsFragment.adapter = adapter
        getTeams()
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://v3.football.api-sports.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    private fun getTeams(){
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(SoccerService::class.java).getTeamsByLeague()
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
        fun newInstance(param1: String, param2: String) =
            Teams().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
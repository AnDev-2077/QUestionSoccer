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
import android.net.ConnectivityManager
import android.widget.Toast
import com.devapps.questionsoccer.TeamsDetailsActivity
import com.devapps.questionsoccer.adapters.SoccerAdapter
import com.devapps.questionsoccer.databinding.FragmentTeamsByLeagueBinding
import com.devapps.questionsoccer.interfaces.SoccerService
import com.devapps.questionsoccer.items.ResponseItem
import com.google.common.reflect.TypeToken
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val PREFS_NAME = "com.devapps.questionsoccer.PREFS"
private const val TEAMS_KEY_PREFIX = "com.devapps.questionsoccer.TEAMS"

class TeamsByLeague : Fragment() {

    private var leagueId: Int? = null

    private lateinit var binding: FragmentTeamsByLeagueBinding
    lateinit var adapter: SoccerAdapter
    private var TeamsFragmentResponse = mutableListOf<ResponseItem>()

    private val db = Firebase.database

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

    private fun getTeams(leagueId: Int) {
        if (isOnline()) {
            CoroutineScope(Dispatchers.IO).launch {
                val call = getRetrofit().create(SoccerService::class.java).getTeamsByLeague(leagueId, 2023)
                val teamsByLeagueResponse = call.body()
                if (call.isSuccessful) {
                    val teams = teamsByLeagueResponse?.response ?: emptyList()
                    withContext(Dispatchers.Main) {
                        TeamsFragmentResponse.clear()
                        TeamsFragmentResponse.addAll(teams)
                        adapter.notifyDataSetChanged()
                    }
                    saveTeamsToSharedPreferences(requireContext(), leagueId, teams)
                } else {
                    showError()
                }
            }
        } else {
            loadTeamsFromSharedPreferences(requireContext(), leagueId)?.let { teams ->
                TeamsFragmentResponse.clear()
                TeamsFragmentResponse.addAll(teams)
                adapter.notifyDataSetChanged()
            } ?: showError()
        }
    }

    private fun saveTeamsToSharedPreferences(context: Context, leagueId: Int, teams: List<ResponseItem>) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(teams)
        editor.putString("$TEAMS_KEY_PREFIX$leagueId", json)
        editor.apply()
    }

    private fun loadTeamsFromSharedPreferences(context: Context, leagueId: Int): List<ResponseItem>? {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("$TEAMS_KEY_PREFIX$leagueId", null)
        return if (json != null) {
            val type = object : TypeToken<List<ResponseItem>>() {}.type
            gson.fromJson(json, type)
        } else {
            null
        }
    }

    private fun isOnline():Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun showError() {
        Toast.makeText(requireContext(), "Error: Sin conección a internet", Toast.LENGTH_SHORT).show()
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
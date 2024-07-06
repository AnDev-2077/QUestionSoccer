package com.devapps.questionsoccer.league_fragments

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.devapps.questionsoccer.adapters.StandingsAdapter
import com.devapps.questionsoccer.databinding.FragmentStandingsByLeagueBinding
import com.devapps.questionsoccer.interfaces.StandingsByLeagueService
import com.devapps.questionsoccer.items.Standing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StandingsByLeague : Fragment() {

    private var leagueId: Int? = null

    private lateinit var binding: FragmentStandingsByLeagueBinding
    private lateinit var adapter: StandingsAdapter
    private var StandingsByLeague = mutableListOf<Standing>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            leagueId = it.getInt("leagueId")
            Log.d("StatisticsByTeam", "Received leagueId: $leagueId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStandingsByLeagueBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = StandingsAdapter(StandingsByLeague)
        binding.rvStandingsFragment.layoutManager = LinearLayoutManager(context)
        binding.rvStandingsFragment.adapter = adapter
        getStandings(leagueId ?: 0)

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

    private fun getStandings(leagueId: Int){
        if(isOnline()){
            CoroutineScope(Dispatchers.IO).launch {
                val call = getRetrofit().create(StandingsByLeagueService::class.java).getStandingsByLeague(leagueId, 2023)
                val standingsResponse = call.body()
                if (call.isSuccessful && standingsResponse != null){
                    val standings = standingsResponse.response
                    withContext(Dispatchers.Main){
                        StandingsByLeague.clear()
                        standings.forEach{standing ->
                            standing.league.standings.forEach { standingList ->
                                StandingsByLeague.addAll(standingList)
                            }
                        }
                        adapter.notifyDataSetChanged()
                    }
                }else{
                    showError()
                }
            }
        }else{
            showError()
        }
    }

    private fun showError() {
        Toast.makeText(requireContext(), "Error: Sin conexión a internet", Toast.LENGTH_SHORT).show()
    }

    private fun isOnline(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
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
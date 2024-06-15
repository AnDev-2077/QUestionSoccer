package com.devapps.questionsoccer.team_fragments

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowId
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.devapps.questionsoccer.R
import com.devapps.questionsoccer.adapters.StatisticsAdapter
import com.devapps.questionsoccer.databinding.FragmentStatisticsByTeamBinding
import com.devapps.questionsoccer.interfaces.StatisticsService
import com.devapps.questionsoccer.items.StaticResponse
import com.google.common.reflect.TypeToken
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
private const val STATISTICS_KEY_PREFIX = "com.devapps.questionsoccer.STATISTICS"

class StatisticsByTeam : Fragment() {
    private var leagueId: Int? = null
    private var teamId: Int? = null

    private lateinit var binding: FragmentStatisticsByTeamBinding
    private lateinit var adapter: StatisticsAdapter
    private var StatisticsFragmentResponse: MutableList<StaticResponse> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            leagueId = it.getInt("leagueId")
            teamId = it.getInt("teamId")

            Log.d("StatisticsByTeam", "Received leagueId: $leagueId")
            Log.d("StatisticsByTeam", "Received teamId: $teamId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatisticsByTeamBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = StatisticsAdapter(StatisticsFragmentResponse)
        binding.rvStatisticsFragment.layoutManager = LinearLayoutManager(context)
        binding.rvStatisticsFragment.adapter = adapter
        getStatistics(leagueId ?: 0 , teamId ?: 0)
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

    /*private fun getStatistics(leagueId: Int, teamId: Int){
        if(isOnline()){
            CoroutineScope(Dispatchers.IO).launch {
                val call = getRetrofit().create(StatisticsService::class.java).getStatistics(leagueId, teamId, 2023)
                val statisticResponse = call.body()
                if (call.isSuccessful){
                    val statistics = statisticResponse?.response
                    withContext(Dispatchers.Main){
                        StatisticsFragmentResponse.clear()
                        if (statistics != null) {
                            StatisticsFragmentResponse.add(statistics)
                        }
                        adapter.notifyDataSetChanged()
                    }
                }else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Error: Sin conexión a la API", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }else{
            StatisticsFragmentResponse.clear()
            adapter.notifyDataSetChanged()
            showError()
        }
    }*/

    private fun getStatistics(leagueId: Int, teamId: Int) {
        if (isOnline()) {
            CoroutineScope(Dispatchers.IO).launch {
                val call = getRetrofit().create(StatisticsService::class.java).getStatistics(leagueId, teamId, 2023)
                val statisticResponse = call.body()
                if (call.isSuccessful) {
                    val statistics = statisticResponse?.response
                    withContext(Dispatchers.Main) {
                        StatisticsFragmentResponse.clear()
                        if (statistics != null) {
                            StatisticsFragmentResponse.add(statistics)
                        }
                        adapter.notifyDataSetChanged()
                    }
                    saveStatisticsToSharedPreferences(requireContext(), leagueId, teamId, statistics)
                } else {
                    showError()
                }
            }
        } else {
            loadStatisticsFromSharedPreferences(requireContext(), leagueId, teamId)?.let { statistics ->
                StatisticsFragmentResponse.clear()
                StatisticsFragmentResponse.add(statistics)
                adapter.notifyDataSetChanged()
            } ?: showError()
        }
    }

    private fun saveStatisticsToSharedPreferences(context: Context, leagueId: Int, teamId: Int, statistics: StaticResponse?) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(statistics)
        editor.putString("$STATISTICS_KEY_PREFIX$leagueId$teamId", json)
        editor.apply()
    }

    private fun loadStatisticsFromSharedPreferences(context: Context, leagueId: Int, teamId: Int): StaticResponse? {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("$STATISTICS_KEY_PREFIX$leagueId$teamId", null)
        return if (json != null) {
            val type = object : TypeToken<StaticResponse>() {}.type
            gson.fromJson(json, type)
        } else {
            null
        }
    }

    private fun showError() {
        Toast.makeText(requireContext(), "Error: Sin coneccion a internet", Toast.LENGTH_SHORT).show()
    }

    private fun isOnline(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    companion object {
        @JvmStatic
        fun newInstance(leagueId: Int, teamId: Int) =
            StatisticsByTeam().apply {
                arguments = Bundle().apply {
                    putInt("leagueId", leagueId)
                    putInt("teamId", teamId)
                }
            }
    }
}
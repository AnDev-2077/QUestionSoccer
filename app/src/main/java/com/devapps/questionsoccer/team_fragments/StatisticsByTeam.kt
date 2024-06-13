package com.devapps.questionsoccer.team_fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowId
import androidx.recyclerview.widget.LinearLayoutManager
import com.devapps.questionsoccer.R
import com.devapps.questionsoccer.adapters.StatisticsAdapter
import com.devapps.questionsoccer.databinding.FragmentStatisticsByTeamBinding
import com.devapps.questionsoccer.interfaces.StatisticsService
import com.devapps.questionsoccer.items.StaticResponse
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

            Log.d("StatisticsByTeam", "Received leagueId and teamId in onCreate: $leagueId")
            Log.d("StatisticsByTeam", "Received leagueId and teamId in onCreate: $teamId")
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

    private fun getStatistics(leagueId: Int, teamId: Int){
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
            }
        }
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
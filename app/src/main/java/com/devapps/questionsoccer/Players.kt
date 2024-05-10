package com.devapps.questionsoccer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.devapps.questionsoccer.adapters.StatisticsAdapter
import com.devapps.questionsoccer.databinding.FragmentLeaguesBinding
import com.devapps.questionsoccer.databinding.FragmentPlayersBinding
import com.devapps.questionsoccer.databinding.ItemFixtureBinding
import com.devapps.questionsoccer.interfaces.StatisticsService
import com.devapps.questionsoccer.items.StaticResponse
import com.devapps.questionsoccer.items.StatisticsResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class Players : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentPlayersBinding
    private lateinit var adapter: StatisticsAdapter
    private var StatisticsFragmentResponse = mutableListOf<StaticResponse>()

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

        binding = FragmentPlayersBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = StatisticsAdapter(StatisticsFragmentResponse)
        binding.rvStatistisFragment.layoutManager = LinearLayoutManager(context)
        binding.rvStatistisFragment.adapter = adapter
        getStatistics()
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://v3.football.api-sports.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getStatistics(){
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(StatisticsService::class.java).getStatistics()
            val statisticResponse = call.body()
            if (call.isSuccessful){
                val statistics = statisticResponse?.response ?: emptyList()
                withContext(Dispatchers.Main){
                    StatisticsFragmentResponse.clear()
                    StatisticsFragmentResponse.addAll(statistics)
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Players().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
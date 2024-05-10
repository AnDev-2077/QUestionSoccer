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
import com.devapps.questionsoccer.databinding.FragmentLeaguesBinding
import com.devapps.questionsoccer.interfaces.LeagueService
import com.devapps.questionsoccer.items.LeagueResponse
import com.devapps.questionsoccer.items.LeagueResponseItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class Leagues : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentLeaguesBinding
    private lateinit var adapter: LeaguesAdapter
    private lateinit var recyclerView: RecyclerView
    private var LeaguesFragmentResponse = mutableListOf<LeagueResponseItem>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLeaguesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = LeaguesAdapter(LeaguesFragmentResponse) { onLeagueClick ->
            val intent = Intent(activity, LeaguesDetailsActivity::class.java)
            intent.putExtra("leagueName", onLeagueClick.league.name)
            intent.putExtra("leagueType", onLeagueClick.league.type)
            intent.putExtra("leagueCountry", onLeagueClick.country.name)
            intent.putExtra("leagueLogo", onLeagueClick.league.logo)
            startActivity(intent)
        }
        binding.rvLeaguesFragment.layoutManager = LinearLayoutManager(context)
        binding.rvLeaguesFragment.adapter = adapter
        getLeagues()
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://v3.football.api-sports.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getLeagues() {
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(LeagueService::class.java).getLeagues()
            val leaguesResponse = call.body()
            if (call.isSuccessful){
                val leagues = leaguesResponse?.response ?: emptyList()
                withContext(Dispatchers.Main) {
                    LeaguesFragmentResponse.clear()
                    LeaguesFragmentResponse.addAll(leagues)
                    adapter.notifyDataSetChanged()
                }

            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Leagues().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }



}
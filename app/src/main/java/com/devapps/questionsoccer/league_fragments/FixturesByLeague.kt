package com.devapps.questionsoccer.league_fragments

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.style.TtsSpan.ARG_YEAR
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.devapps.questionsoccer.FixtureDetailsActivity
import com.devapps.questionsoccer.R
import com.devapps.questionsoccer.adapters.FixtureAdapter
import com.devapps.questionsoccer.databinding.FragmentFixturesByLeagueBinding
import com.devapps.questionsoccer.interfaces.FixturesByLeagueService
import com.devapps.questionsoccer.items.fixtureResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FixturesByLeague : Fragment() {

    private var leagueId: Int? = null
    private var year: Int? = null

    private lateinit var binding: FragmentFixturesByLeagueBinding
    private lateinit var adapter: FixtureAdapter
    private var FixturesFragmentResponse = mutableListOf<fixtureResponse>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            leagueId = it.getInt("leagueId")
            leagueId = it.getInt(ARG_YEAR)
            Log.d("FixturesByLeague", "League ID: $leagueId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFixturesByLeagueBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = FixtureAdapter(FixturesFragmentResponse){}
        binding.rvFixturesFragment.layoutManager = LinearLayoutManager(context)
        binding.rvFixturesFragment.adapter = adapter
        getFixtures(leagueId ?: 0, year ?: 0)
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

    private fun getFixtures(leagueId: Int, year: Int) {
        if(isOnline()){
            CoroutineScope(Dispatchers.IO).launch {
                val call = getRetrofit().create(FixturesByLeagueService::class.java).getFixtureByLeague(leagueId, year)
                val fixtureResponse = call.body()
                if (call.isSuccessful){
                    val fixtures = fixtureResponse?.response ?: emptyList()
                    withContext(Dispatchers.Main){
                        FixturesFragmentResponse.clear()
                        FixturesFragmentResponse.addAll(fixtures)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }else {
            showError()
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
        fun newInstance(leagueId: Int, year: Int) =
            FixturesByLeague().apply {
                arguments = Bundle().apply {
                    putInt("leagueId", leagueId)
                    putInt(ARG_YEAR, year)
                }
            }
    }
}
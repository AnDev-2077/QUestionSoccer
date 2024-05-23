package com.devapps.questionsoccer

import android.os.Bundle
import android.util.Log

import androidx.appcompat.app.AppCompatActivity
import com.devapps.questionsoccer.adapters.ViewPagerAdapter
import com.devapps.questionsoccer.databinding.ActivityFixtureDetailsBinding
import com.devapps.questionsoccer.interfaces.LineupService
import com.devapps.questionsoccer.subfragments.Lineup
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FixtureDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFixtureDetailsBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFixtureDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPagerAdapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = viewPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Alineación"
                else -> "Estadisticas"
            }
        }.attach()

        val fixtureId = intent.getIntExtra("id",-1)
        Log.d("FixtureDetailsActivity", "fixtureId: $fixtureId")
        getLineups()
    }
    private fun getRetrofit(): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
        return Retrofit.Builder()
            .baseUrl("https://v3.football.api-sports.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    private fun getLineups(){
        CoroutineScope(Dispatchers.IO).launch {
            val fixtureId = intent.getIntExtra("id",-1)
            Log.d("FixtureDetailsActivity", "Sending fixtureId: $fixtureId")
            val call = getRetrofit().create(LineupService::class.java).getLineup(fixtureId.toString())
            val lineupResponse = call.body()
            if(call.isSuccessful && lineupResponse?.response?.isNotEmpty() == true){
                val teamHome = lineupResponse.response.get(0).team
                val teamAway = lineupResponse.response.get(1).team
                val lineUpHome = lineupResponse.response.get(0).startXI
                val lineUpAway = lineupResponse.response.get(1).startXI
                val coachHome = lineupResponse.response.get(0).coach
                val coachAway = lineupResponse.response.get(1).coach

                withContext(Dispatchers.Main){
                    binding.tvHome.text = teamHome?.name
                    binding.tvAway.text = teamAway?.name
                    Picasso.get().load(teamHome?.logo).into(binding.ivHome)
                    Picasso.get().load(teamAway?.logo).into(binding.ivAway)

                    val lineupFragment = Lineup.newInstance(
                        teamHome,
                        teamAway,
                        lineUpHome,
                        lineUpAway,
                        coachHome,
                        coachAway
                    )
                    viewPagerAdapter.addFragment(lineupFragment)
                }
            }
        }
    }

}
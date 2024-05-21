package com.devapps.questionsoccer

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.devapps.questionsoccer.adapters.LeaguesAdapter
import com.devapps.questionsoccer.databinding.ActivityLeaguesDetailsBinding
import com.squareup.picasso.Picasso

class LeaguesDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLeaguesDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaguesDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val teamLogo = intent.getStringExtra("leagueLogo")
        val leagueName = intent.getStringExtra("leagueName")
        val leagueType = intent.getStringExtra("leagueType")
        val leagueCountry = intent.getStringExtra("leagueCountry")

        Picasso.get().load(teamLogo).into(binding.ivLeagueLogo)

        binding.tvLeagueName.text = leagueName
        binding.tvLeagueType.text = leagueType
        binding.tvLeagueCountry.text = leagueCountry


    }
}
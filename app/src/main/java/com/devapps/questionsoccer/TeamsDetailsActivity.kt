package com.devapps.questionsoccer

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.devapps.questionsoccer.databinding.ActivityTeamsDetailsBinding
import com.squareup.picasso.Picasso

class TeamsDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeamsDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTeamsDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val teamLogo = intent.getStringExtra("teamLogo")
        val teamName = intent.getStringExtra("teamName")
        val teamCode = intent.getStringExtra("teamCode")
        val teamCountry = intent.getStringExtra("teamCountry")
        val venueName = intent.getStringExtra("venueName")
        val venueAddress = intent.getStringExtra("venueAddress")
        val venueCity = intent.getStringExtra("venueCity")
        val venueCapacity = intent.getIntExtra("venueCapacity",0)
        val venueImage = intent.getStringExtra("venueImage")

        Picasso.get().load(teamLogo).into(binding.ivTeamLogo)
        Picasso.get().load(venueImage).into(binding.ivVenueImage)

        binding.tvTeamName.text = teamName
        binding.tvTeamCode.text = teamCode
        binding.tvTeamCountry.text = teamCountry
        binding.tvVenueName.text= venueName
        binding.tvVenueAddress.text = venueAddress
        binding.tvVenueCity.text = venueCity
        binding.tvVenueCapacity.text = venueCapacity.toString()

    }
}
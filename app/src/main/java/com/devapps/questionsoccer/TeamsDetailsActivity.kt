package com.devapps.questionsoccer

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.devapps.questionsoccer.adapters.LeaguesPagerAdapter
import com.devapps.questionsoccer.adapters.TeamsPagerAdapter
import com.devapps.questionsoccer.databinding.ActivityTeamsDetailsBinding
import com.google.android.material.tabs.TabLayout
import com.squareup.picasso.Picasso

class TeamsDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeamsDetailsBinding
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var adapter: TeamsPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        //Picasso.get().load(venueImage).into(binding.ivVenueImage)

        binding.tvTeamName.text = teamName
        binding.tvTeamCode.text = teamCode
        binding.tvTeamCountry.text = teamCountry

        tabLayout = binding.tabTeams
        viewPager2 = binding.viewPagerTeams

        adapter = TeamsPagerAdapter(supportFragmentManager, lifecycle)
        tabLayout.addTab(tabLayout.newTab().setText("Partidos"))
        tabLayout.addTab(tabLayout.newTab().setText("Estadisticas"))

        viewPager2.adapter = adapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager2.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })
        //binding.tvVenueName.text= venueName
        //binding.tvVenueAddress.text = venueAddress
        //binding.tvVenueCity.text = venueCity
        //binding.tvVenueCapacity.text = venueCapacity.toString()

    }
}
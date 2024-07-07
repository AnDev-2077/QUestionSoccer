package com.devapps.questionsoccer

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.devapps.questionsoccer.adapters.LeaguesAdapter
import com.devapps.questionsoccer.adapters.LeaguesPagerAdapter
import com.devapps.questionsoccer.databinding.ActivityLeaguesDetailsBinding
import com.devapps.questionsoccer.items.Season
import com.google.android.material.tabs.TabLayout
import com.squareup.picasso.Picasso

class LeaguesDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLeaguesDetailsBinding
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var adapter: LeaguesPagerAdapter
    private lateinit var seasonsSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaguesDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val teamLogo = intent.getStringExtra("leagueLogo")
        val leagueName = intent.getStringExtra("leagueName")
        val leagueType = intent.getStringExtra("leagueType")
        val leagueCountry = intent.getStringExtra("leagueCountry")
        val leagueId = intent.getIntExtra("leagueId", 39)
        val leagueSeasons = intent.getSerializableExtra("leagueSeasons") as? ArrayList<Season>

        Picasso.get().load(teamLogo).into(binding.ivLeagueLogo)
        binding.tvLeagueName.text = leagueName
        binding.tvLeagueType.text = leagueType
        binding.tvLeagueCountry.text = leagueCountry

        seasonsSpinner = binding.spSeasons

        if (leagueSeasons != null) {
            setupSpinner(leagueSeasons)
        }

        setupViewPagerAndTabs(leagueId)
    }

    private fun setupSpinner(seasons: List<Season>) {
        val seasonYears = seasons.map { it.year.toString() }
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, seasonYears)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        seasonsSpinner.adapter = spinnerAdapter
        val params = seasonsSpinner.layoutParams
        seasonsSpinner.layoutParams = params
    }

    private fun setupViewPagerAndTabs(leagueId: Int){
        tabLayout = binding.tabLeagues
        viewPager2 = binding.viewPagerLeagues

        adapter = LeaguesPagerAdapter(supportFragmentManager, lifecycle, leagueId)
        tabLayout.addTab(tabLayout.newTab().setText("Equipos"))
        tabLayout.addTab(tabLayout.newTab().setText("Partidos"))
        tabLayout.addTab(tabLayout.newTab().setText("Clasificaciones"))

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
    }
}
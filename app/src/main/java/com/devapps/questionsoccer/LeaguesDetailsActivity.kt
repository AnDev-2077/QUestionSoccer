package com.devapps.questionsoccer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
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
    var leagueId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaguesDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val teamLogo = intent.getStringExtra("leagueLogo")
        val leagueName = intent.getStringExtra("leagueName")
        val leagueType = intent.getStringExtra("leagueType")
        val leagueCountry = intent.getStringExtra("leagueCountry")
        val leagueSeasons = intent.getSerializableExtra("leagueSeasons") as? ArrayList<Season>
        leagueId = intent.getIntExtra("leagueId", 39)

        Log.w("LeaguesDetailsActivity", "leagueId received: $leagueId")
        Picasso.get().load(teamLogo).into(binding.ivLeagueLogo)
        binding.tvLeagueName.text = leagueName
        binding.tvLeagueType.text = leagueType
        binding.tvLeagueCountry.text = leagueCountry

        seasonsSpinner = binding.spSeasons

        if (leagueSeasons != null) {
            setupSpinner(leagueSeasons)
        }

    }

    private fun setupSpinner(seasons: List<Season>) {

        val sortedSeasons = seasons.sortedByDescending { it.year }

        val seasonYears = sortedSeasons.map { it.year.toString() }
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, seasonYears)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        seasonsSpinner.adapter = spinnerAdapter
        val params = seasonsSpinner.layoutParams
        seasonsSpinner.layoutParams = params
        seasonsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedYear = seasonYears[position]
                sendSelectedYear(selectedYear.toInt())
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    private fun sendSelectedYear(year: Int) {
        val intent = Intent(this, TeamsDetailsActivity::class.java)
        intent.putExtra("selectedYear", year)
        setupViewPagerAndTabs(leagueId, year)
        Toast.makeText(this, "Año seleccionado: $year", Toast.LENGTH_SHORT).show()
    }

    private fun setupViewPagerAndTabs(leagueId: Int, year: Int){
        tabLayout = binding.tabLeagues
        viewPager2 = binding.viewPagerLeagues

        tabLayout.removeAllTabs()

        adapter = LeaguesPagerAdapter(supportFragmentManager, lifecycle, leagueId, year)
        viewPager2.adapter = adapter

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
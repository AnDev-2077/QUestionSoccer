package com.devapps.questionsoccer

import SoccerDbHelper
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devapps.questionsoccer.adapters.LeaguesAdapter
import com.devapps.questionsoccer.databinding.FragmentLeaguesBinding
import com.devapps.questionsoccer.interfaces.LeagueService
import com.devapps.questionsoccer.items.LeagueResponse
import com.devapps.questionsoccer.items.LeagueResponseItem
import com.devapps.questionsoccer.items.TeamsByLeagueResponse
import com.devapps.questionsoccer.league_fragments.TeamsByLeague
import com.google.common.reflect.TypeToken
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private const val PREFS_NAME = "com.devapps.questionsoccer.PREFS"
private const val LEAGUES_KEY = "com.devapps.questionsoccer.LEAGUES"
class Leagues : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentLeaguesBinding
    lateinit var adapter: LeaguesAdapter
    private var LeaguesFragmentResponse = mutableListOf<LeagueResponseItem>()
    private val db = Firebase.firestore
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
            intent.putExtra("leagueId", onLeagueClick.league.id)
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
        if (isOnline()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val call = getRetrofit().create(LeagueService::class.java).getLeagues()
                    val leaguesResponse = call.body()
                    if (call.isSuccessful) {
                        val leagues = leaguesResponse?.response ?: emptyList()
                        withContext(Dispatchers.Main) {
                            LeaguesFragmentResponse.clear()
                            LeaguesFragmentResponse.addAll(leagues)
                            adapter.notifyDataSetChanged()
                            showRecyclerView()
                        }
                        saveLeaguesToDatabase(requireContext(), leagues)
                    } else {
                        showError()
                    }
                } catch (e: Exception) {
                    Log.d("Leagues", "Error: ${e.message}")
                }
            }
        } else {
            loadLeaguesFromDatabase(requireContext())?.let { leagues ->
                LeaguesFragmentResponse.clear()
                LeaguesFragmentResponse.addAll(leagues)
                adapter.notifyDataSetChanged()
            } ?: showError()
        }
    }
    private fun saveLeaguesToDatabase(context: Context, leagues: List<LeagueResponseItem>) {
        val dbHelper = SoccerDbHelper(context)
        val db = dbHelper.writableDatabase

        db.beginTransaction()
        try {
            for (leagueItem in leagues) {
                val leagueValues = ContentValues().apply {
                    put("id", leagueItem.league.id)
                    put("name", leagueItem.league.name)
                    put("type", leagueItem.league.type)
                    put("logo", leagueItem.league.logo)
                    put("country_name", leagueItem.country.name)
                    put("country_code", leagueItem.country.code.toString()) //por que son null o any
                    put("country_flag", leagueItem.country.flag.toString())
                }
                db.insertWithOnConflict("leagues", null, leagueValues, SQLiteDatabase.CONFLICT_REPLACE)

                for (season in leagueItem.seasons) {
                    val seasonValues = ContentValues().apply {
                        put("league_id", leagueItem.league.id)
                        put("year", season.year)
                        put("start_date", season.start)
                        put("end_date", season.end)
                        put("current", season.current)
                        put("coverage_fixtures_events", season.coverage.fixtures.events)
                        put("coverage_fixtures_lineups", season.coverage.fixtures.lineups)
                        put("coverage_fixtures_statistics_fixtures", season.coverage.fixtures.statistics_fixtures)
                        put("coverage_fixtures_statistics_players", season.coverage.fixtures.statistics_players)
                        put("coverage_standings", season.coverage.standings)
                        put("coverage_players", season.coverage.players)
                        put("coverage_top_scorers", season.coverage.top_scorers)
                        put("coverage_top_assists", season.coverage.top_assists)
                        put("coverage_top_cards", season.coverage.top_cards)
                        put("coverage_injuries", season.coverage.injuries)
                        put("coverage_predictions", season.coverage.predictions)
                        put("coverage_odds", season.coverage.odds)
                    }
                    db.insertWithOnConflict("seasons", null, seasonValues, SQLiteDatabase.CONFLICT_REPLACE)
                }
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        db.close()
    }
    private fun loadLeaguesFromDatabase(context: Context): List<LeagueResponseItem> {
        val dbHelper = SoccerDbHelper(context)
        val db = dbHelper.readableDatabase

        val leagues = mutableListOf<LeagueResponseItem>()

        val cursorLeagues = db.rawQuery("SELECT * FROM leagues", null)
        if (cursorLeagues.moveToFirst()) {
            do {
                val leagueId = cursorLeagues.getInt(cursorLeagues.getColumnIndexOrThrow("id"))
                val name = cursorLeagues.getString(cursorLeagues.getColumnIndexOrThrow("name"))
                val type = cursorLeagues.getString(cursorLeagues.getColumnIndexOrThrow("type"))
                val logo = cursorLeagues.getString(cursorLeagues.getColumnIndexOrThrow("logo"))
                val countryName = cursorLeagues.getString(cursorLeagues.getColumnIndexOrThrow("country_name"))
                val countryCode = cursorLeagues.getString(cursorLeagues.getColumnIndexOrThrow("country_code"))
                val countryFlag = cursorLeagues.getString(cursorLeagues.getColumnIndexOrThrow("country_flag"))

                val league = LeagueResponseItem.league(leagueId, name, type, logo)
                val country = LeagueResponseItem.country(countryName, countryCode, countryFlag)

                val cursorSeasons = db.rawQuery("SELECT * FROM seasons WHERE league_id = ?", arrayOf(leagueId.toString()))
                val seasons = mutableListOf<LeagueResponseItem.Season>()
                if (cursorSeasons.moveToFirst()) {
                    do {
                        val year = cursorSeasons.getInt(cursorSeasons.getColumnIndexOrThrow("year"))
                        val startDate = cursorSeasons.getString(cursorSeasons.getColumnIndexOrThrow("start_date"))
                        val endDate = cursorSeasons.getString(cursorSeasons.getColumnIndexOrThrow("end_date"))
                        val current = cursorSeasons.getInt(cursorSeasons.getColumnIndexOrThrow("current")) > 0
                        val coverageFixturesEvents = cursorSeasons.getInt(cursorSeasons.getColumnIndexOrThrow("coverage_fixtures_events")) > 0
                        val coverageFixturesLineups = cursorSeasons.getInt(cursorSeasons.getColumnIndexOrThrow("coverage_fixtures_lineups")) > 0
                        val coverageFixturesStatisticsFixtures = cursorSeasons.getInt(cursorSeasons.getColumnIndexOrThrow("coverage_fixtures_statistics_fixtures")) > 0
                        val coverageFixturesStatisticsPlayers = cursorSeasons.getInt(cursorSeasons.getColumnIndexOrThrow("coverage_fixtures_statistics_players")) > 0
                        val coverageStandings = cursorSeasons.getInt(cursorSeasons.getColumnIndexOrThrow("coverage_standings")) > 0
                        val coveragePlayers = cursorSeasons.getInt(cursorSeasons.getColumnIndexOrThrow("coverage_players")) > 0
                        val coverageTopScorers = cursorSeasons.getInt(cursorSeasons.getColumnIndexOrThrow("coverage_top_scorers")) > 0
                        val coverageTopAssists = cursorSeasons.getInt(cursorSeasons.getColumnIndexOrThrow("coverage_top_assists")) > 0
                        val coverageTopCards = cursorSeasons.getInt(cursorSeasons.getColumnIndexOrThrow("coverage_top_cards")) > 0
                        val coverageInjuries = cursorSeasons.getInt(cursorSeasons.getColumnIndexOrThrow("coverage_injuries")) > 0
                        val coveragePredictions = cursorSeasons.getInt(cursorSeasons.getColumnIndexOrThrow("coverage_predictions")) > 0
                        val coverageOdds = cursorSeasons.getInt(cursorSeasons.getColumnIndexOrThrow("coverage_odds")) > 0

                        val coverage = LeagueResponseItem.Coverage(
                            LeagueResponseItem.Coverage.Fixtures(
                                coverageFixturesEvents, coverageFixturesLineups, coverageFixturesStatisticsFixtures, coverageFixturesStatisticsPlayers
                            ),
                            coverageStandings, coveragePlayers, coverageTopScorers, coverageTopAssists, coverageTopCards, coverageInjuries, coveragePredictions, coverageOdds
                        )
                        seasons.add(LeagueResponseItem.Season(year, startDate, endDate, current, coverage))
                    } while (cursorSeasons.moveToNext())
                }
                cursorSeasons.close()

                leagues.add(LeagueResponseItem(league, country, seasons))
            } while (cursorLeagues.moveToNext())
        }
        cursorLeagues.close()
        db.close()

        return leagues
    }




    /*private fun saveLeaguesToSharedPreferences(context: Context, leagues: List<LeagueResponseItem>) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(leagues)
        editor.putString(LEAGUES_KEY, json)
        editor.apply()
    }

    private fun loadLeaguesFromSharedPreferences(context: Context): List<LeagueResponseItem>? {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString(LEAGUES_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<List<LeagueResponseItem>>() {}.type
            gson.fromJson(json, type)
        } else {
            null
        }
    }*/

    private fun isOnline():Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
    private fun showError() {
        Toast.makeText(requireContext(), "Error: Sin conección a internet", Toast.LENGTH_SHORT).show()
        showNoInternetImage()
    }
    private fun showError0() {
        Toast.makeText(requireContext(), "Error 0: Sin conección a internet, pasando a modo sin conección", Toast.LENGTH_SHORT).show()
    }

    private fun showRecyclerView() {
        binding.rvLeaguesFragment.visibility = View.VISIBLE
        binding.ivNoInternet.visibility = View.GONE
    }

    private fun showNoInternetImage() {
        binding.rvLeaguesFragment.visibility = View.GONE
        binding.ivNoInternet.visibility = View.VISIBLE
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
package com.devapps.questionsoccer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
    private lateinit var networkChangeReceiver: NetworkChangeReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        networkChangeReceiver = NetworkChangeReceiver{ isConnected ->
            if (isConnected) {
                getLeagues()
            } else {
                loadLeaguesFromSharedPreferences(requireContext())?.let { leagues ->
                    LeaguesFragmentResponse.clear()
                    LeaguesFragmentResponse.addAll(leagues)
                    adapter.notifyDataSetChanged()
                } ?: run {
                    showError()
                    binding.rvLeaguesFragment.visibility = View.GONE
                    binding.ivNoInternet.visibility = View.VISIBLE
                }
            }
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

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        requireContext().registerReceiver(networkChangeReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        requireContext().unregisterReceiver(networkChangeReceiver)
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
                        }
                        saveLeaguesToSharedPreferences(requireContext(), leagues)
                    } else {
                        showError()
                    }
                } catch (e: Exception) {
                    Log.d("Leagues", "Error: ${e.message}")
                }
            }
        } else {
            loadLeaguesFromSharedPreferences(requireContext())?.let { leagues ->
                LeaguesFragmentResponse.clear()
                LeaguesFragmentResponse.addAll(leagues)
                adapter.notifyDataSetChanged()
            } ?: run{
                showError()
                val noInternetImageView: ImageView = binding.ivNoInternet
                binding.rvLeaguesFragment.visibility = View.GONE
                noInternetImageView.visibility = View.VISIBLE
            }

        }
    }

    /*private fun saveLeaguesToSharedPreferences(context: Context, leagues: List<LeagueResponseItem>) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(leagues)
        editor.putString(LEAGUES_KEY, json)
        editor.apply()
    }*/

    private fun saveLeaguesToSharedPreferences(context: Context, leagues: List<LeagueResponseItem>) {
        val limitedLeagues = leagues.take(30)
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(limitedLeagues)
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
    }

    private fun isOnline():Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
    private fun showError() {
        Toast.makeText(requireContext(), "Error: Sin conección a internet", Toast.LENGTH_SHORT).show()
    }

    inner class NetworkChangeReceiver(private val onNetworkChange: (Boolean) -> Unit) : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            val isConnected = networkCapabilities != null &&
                    (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
            onNetworkChange(isConnected)
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


/*private fun getLeagues() {
        if(isOnline()){
            CoroutineScope(Dispatchers.IO).launch {
                try{
                    val call = getRetrofit().create(LeagueService::class.java).getLeagues()
                    val leaguesResponse = call.body()
                    if (call.isSuccessful){
                        val leagues = leaguesResponse?.response ?: emptyList()
                        withContext(Dispatchers.Main) {
                            LeaguesFragmentResponse.clear()
                            LeaguesFragmentResponse.addAll(leagues)
                            adapter.notifyDataSetChanged()
                        }
                        saveLeaguesToRealtimeDatabase(leagues)
                    } else{
                        showError()
                    }
                }catch (e: Exception){
                    Log.d("Leagues", "Error: ${e.message}")
                }

            }
        } else{
            loadLeaguesFromRealtimeDatabase()
            showError0()
        }
    }*/

/*private suspend fun saveLeaguesToRealtimeDatabase(leagues: List<LeagueResponseItem>) {
    try {
        val limitedLeagues = leagues.take(20)
        Firebase.database.reference.child("leagues").setValue(limitedLeagues)
    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            showError()
        }
    }
}

private fun loadLeaguesFromRealtimeDatabase() {
    Firebase.database.reference.child("leagues").get().addOnSuccessListener { dataSnapshot ->
        val leagues = dataSnapshot.getValue(object : GenericTypeIndicator<ArrayList<LeagueResponseItem>>() {})
        if (leagues != null) {
            LeaguesFragmentResponse.clear()
            LeaguesFragmentResponse.addAll(leagues)
            adapter.notifyDataSetChanged()
        }
    }.addOnFailureListener {
        showError()
    }
}*/
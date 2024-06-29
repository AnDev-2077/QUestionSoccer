package com.devapps.questionsoccer

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.devapps.questionsoccer.adapters.SoccerAdapter
import com.devapps.questionsoccer.databinding.FragmentFavoritesTestBinding
import com.devapps.questionsoccer.items.ResponseItem
import com.devapps.questionsoccer.items.StaticResponse
import com.devapps.questionsoccer.items.fixtureResponse
import com.google.common.reflect.TypeToken
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

private const val PREFS_NAME = "com.devapps.questionsoccer.PREFS"
private const val FAVORITES_KEY = "com.devapps.questionsoccer.FAVORITES"


class Favorites : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: FragmentFavoritesTestBinding
    private lateinit var teamsAdapter: SoccerAdapter
    var favoritesTeamsList = mutableListOf<ResponseItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoritesTestBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        teamsAdapter = SoccerAdapter(favoritesTeamsList) { onTeamClick ->
                val intent = Intent(activity, TeamsDetailsActivity::class.java)
                intent.putExtra("teamId", onTeamClick.team.teamId.toInt())
                intent.putExtra("teamLogo", onTeamClick.team.teamLogo)
                intent.putExtra("teamName", onTeamClick.team.teamName)
                intent.putExtra("teamCode", onTeamClick.team.teamCode)
                intent.putExtra("teamCountry", onTeamClick.team.teamCountry)
                intent.putExtra("venueName", onTeamClick.venue.venueName)
                intent.putExtra("venueAddress", onTeamClick.venue.venueAddress)
                intent.putExtra("venueCity", onTeamClick.venue.venueCity)
                intent.putExtra("venueCapacity", onTeamClick.venue.venueCapacity)
                intent.putExtra("venueImage", onTeamClick.venue.venueImage)
                startActivity(intent)

        }
        binding.rvTeamsFavorites.layoutManager = LinearLayoutManager(context)
        binding.rvTeamsFavorites.adapter = teamsAdapter
        getFavorites()
    }




    private fun getFavorites() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val db = Firebase.firestore
            db.collection("users").document(user.uid).collection("favorites")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        showError()
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        favoritesTeamsList.clear()
                        for (document in snapshot.documents) {
                            val team = document.toObject(ResponseItem::class.java)
                            if (team != null) {
                                favoritesTeamsList.add(team)
                            }
                        }
                        teamsAdapter.notifyDataSetChanged()
                    } else {
                        showError()
                    }
                }
        }
    }

    private fun isOnline(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun showError() {
        Toast.makeText(requireContext(), "Error: Sin conexión a internet", Toast.LENGTH_SHORT).show()
    }



    companion object {
        @JvmStatic
        fun newInstance() =
            Favorites().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
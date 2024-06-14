package com.devapps.questionsoccer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.devapps.questionsoccer.adapters.SoccerAdapter
import com.devapps.questionsoccer.databinding.FragmentFavoritesTestBinding
import com.devapps.questionsoccer.items.ResponseItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Favorites_Test : Fragment() {

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
            startActivity(intent)

        }
        binding.rvTeamsFavorites.layoutManager = LinearLayoutManager(context)
        binding.rvTeamsFavorites.adapter = teamsAdapter
        getFavorites()
    }

    private fun getFavorites() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(user.uid).collection("favorites").get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val team = document.toObject(ResponseItem::class.java)
                        favoritesTeamsList.add(team)
                    }
                    teamsAdapter.notifyDataSetChanged()
                }
        }

    }


    companion object {
        @JvmStatic
        fun newInstance() =
            Favorites_Test().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
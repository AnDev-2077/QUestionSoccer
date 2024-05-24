package com.devapps.questionsoccer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.devapps.questionsoccer.adapters.LeaguesAdapter
import com.devapps.questionsoccer.databinding.FragmentFavoritesTestBinding
import com.devapps.questionsoccer.items.LeagueResponseItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Favorites_Test : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentFavoritesTestBinding
    private lateinit var adapter: LeaguesAdapter
    var favoritesList = mutableListOf<LeagueResponseItem>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
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
        adapter = LeaguesAdapter(favoritesList) { onLeagueClick ->
            // Handle league click
        }
        binding.rvFavorites.layoutManager = LinearLayoutManager(context)
        binding.rvFavorites.adapter = adapter
        getFavorites()
    }

    private fun getFavorites() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(user.uid).collection("favorites").get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val league = document.toObject(LeagueResponseItem::class.java)
                        favoritesList.add(league)
                    }
                    adapter.notifyDataSetChanged()
                }
        }
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Favorites_Test().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
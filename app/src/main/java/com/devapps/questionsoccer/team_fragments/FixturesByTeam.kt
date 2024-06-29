package com.devapps.questionsoccer.team_fragments

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
import com.devapps.questionsoccer.FixtureDetailsActivity
import com.devapps.questionsoccer.R
import com.devapps.questionsoccer.adapters.FixtureAdapter
import com.devapps.questionsoccer.databinding.FragmentFixturesByTeamBinding
import com.devapps.questionsoccer.interfaces.FixtureService
import com.devapps.questionsoccer.interfaces.FixturesByTeamService
import com.devapps.questionsoccer.items.fixtureResponse
import com.google.common.reflect.TypeToken
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val PREFS_NAME = "com.devapps.questionsoccer.PREFS"
private const val FIXTURES_KEY_PREFIX = "com.devapps.questionsoccer.FIXTURES"

class FixturesByTeam : Fragment() {

    private var teamId: Int? = null


    private lateinit var binding: FragmentFixturesByTeamBinding
    private lateinit var adapter: FixtureAdapter
    private var FixturesFragmentResponse = mutableListOf<fixtureResponse>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            teamId = it.getInt("teamId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentFixturesByTeamBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = FixtureAdapter(FixturesFragmentResponse) { onFixtureClick ->
            val intent = Intent(activity, FixtureDetailsActivity::class.java)
            intent.putExtra("id", onFixtureClick.fixture.id)
            startActivity(intent)
        }
        binding.rvFixturesFragment.layoutManager = LinearLayoutManager(context)
        binding.rvFixturesFragment.adapter = adapter
        getFixtures(teamId ?: 0)
    }

    private fun getRetrofit(): Retrofit {

        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val client = OkHttpClient.Builder().addInterceptor(logging).build()
        return Retrofit.Builder()
            .baseUrl("https://v3.football.api-sports.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    private fun getFixtures(teamId: Int) {
        if (isOnline()) {
            CoroutineScope(Dispatchers.IO).launch {
                val call = getRetrofit().create(FixturesByTeamService::class.java).getFixtureByTeam(teamId, 2023)
                val fixtureResponse = call.body()
                if (call.isSuccessful) {
                    val fixtures = fixtureResponse?.response ?: emptyList()
                    withContext(Dispatchers.Main) {
                        FixturesFragmentResponse.clear()
                        FixturesFragmentResponse.addAll(fixtures)
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    showError()
                }
            }
        } else {
            loadFixturesFromFirestore(teamId)
        }
    }

    private fun loadFixturesFromFirestore(teamId: Int) {
        val db = FirebaseFirestore.getInstance()
        val teamDocument = db.collection("teams").document(teamId.toString())
        teamDocument.collection("fixtures").get()
            .addOnSuccessListener { documents ->
                val fixtures = documents.mapNotNull { it.toObject(fixtureResponse::class.java) }
                FixturesFragmentResponse.clear()
                FixturesFragmentResponse.addAll(fixtures)
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error al obtener fixtures", exception)
                showError()
            }
    }

    private fun showError() {
        Toast.makeText(requireContext(), "Error: Sin coneccion a internet", Toast.LENGTH_SHORT).show()
    }

    private fun isOnline(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    companion object {
        @JvmStatic
        fun newInstance(teamId: Int) =
            FixturesByTeam().apply {
                arguments = Bundle().apply {
                    putInt("teamId", teamId)
                }
            }
    }
}
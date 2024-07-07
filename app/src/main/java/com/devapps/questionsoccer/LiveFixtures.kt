package com.devapps.questionsoccer

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.devapps.questionsoccer.adapters.FixtureAdapter
import com.devapps.questionsoccer.databinding.FragmentLiveFixturesBinding
import com.devapps.questionsoccer.interfaces.FixtureService
import com.devapps.questionsoccer.items.fixtureResponse
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private const val PREFS_NAME = "com.devapps.questionsoccer.PREFS"
private const val FIXTURES_KEY = "com.devapps.questionsoccer.FIXTURES"

class LiveFixtures : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentLiveFixturesBinding
    private lateinit var adapter: FixtureAdapter
    private var FixturesFragmentResponse = mutableListOf<fixtureResponse>()

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
        binding = FragmentLiveFixturesBinding.inflate(inflater, container, false)
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
        getFixtures()
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

    /*private fun getFixtures(){
        if (isOnline()){
            CoroutineScope(Dispatchers.IO).launch {
                val call = getRetrofit().create(FixtureService::class.java).getFixtureByLeague()
                val fixtureResponse = call.body()
                if (call.isSuccessful){
                    val fixtures = fixtureResponse?.response ?: emptyList()
                    withContext(Dispatchers.Main){
                        FixturesFragmentResponse.clear()
                        FixturesFragmentResponse.addAll(fixtures)
                        adapter.notifyDataSetChanged()
                    }
                }  else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Error: En la consulta a la API", Toast.LENGTH_SHORT).show()

                    }
                }
            }
        }else{
            FixturesFragmentResponse.clear()
            adapter.notifyDataSetChanged()
            showError()
        }

    }*/
    private fun getFixtures(){
        if (isOnline()){
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val call = getRetrofit().create(FixtureService::class.java).getFixtureByLeague()
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
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            showError()
            val noInternetImageView: ImageView = binding.ivNoInternet
            binding.rvFixturesFragment.visibility = View.GONE
            noInternetImageView.visibility = View.VISIBLE
        }
    }



    private fun showError() {
        Toast.makeText(requireContext(), "Error: Sin conección a internet", Toast.LENGTH_SHORT).show()
    }

    private fun isOnline():Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LiveFixtures().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
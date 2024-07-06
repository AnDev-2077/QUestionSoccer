package com.devapps.questionsoccer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import com.devapps.questionsoccer.databinding.ActivityMainBinding
import com.devapps.questionsoccer.interfaces.FixturesByTeamService
import com.devapps.questionsoccer.interfaces.LineupService
import com.devapps.questionsoccer.items.LineupResponse
import com.devapps.questionsoccer.items.fixtureResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


enum class ProviderType{
    BASIC,
    GOOGLE
}
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var networkChangeReceiver: NetworkChangeReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(Leagues())
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.leagues -> replaceFragment(Leagues())
                //R.id.teams -> replaceFragment(Teams())
                //R.id.players -> replaceFragment(Players())
                R.id.favorites -> replaceFragment(LiveFixtures())
                R.id.countries -> replaceFragment(Countries())
                R.id.profile -> replaceFragment(Auth())
                //R.id.favorites_test -> replaceFragment(Favorites_Test())
                else ->{
                }
            }
            true
        }

        networkChangeReceiver = NetworkChangeReceiver{ isConnected ->
            if (isOnline()) {
                showOnlineLayout()
                FirebaseDatabase.getInstance().setPersistenceEnabled(true)
                val settings = firestoreSettings {
                    isPersistenceEnabled = true
                }
                Firebase.firestore.firestoreSettings = settings

                val user = FirebaseAuth.getInstance().currentUser
                if(user != null){
                    // El usuario ha iniciado sesión
                    // Verificar si el usuario tiene equipos en sus favoritos
                    val db = FirebaseFirestore.getInstance()
                    db.collection("users").document(user.uid).collection("favorites").get()
                        .addOnSuccessListener { documents ->
                            if(!documents.isEmpty){

                                Log.d("Firestore", "Favoritos encontrados para el usuario: ${user.uid}")

                                // El usuario tiene equipos en sus favoritos
                                // Hacer peticiones a la API para obtener la información de los equipos, sus partidos y estadísticas

                                for (document in documents){
                                    val teamId = document.id as? String
                                    if (teamId != null) {
                                        Log.d("TeamId", "El ID del equipo es: $teamId")
                                        CoroutineScope(Dispatchers.IO).launch {
                                            val fixtures = getFixturesByTeam(teamId)
                                            if (fixtures != null) {
                                                // Guardar las fixtures en Firestore
                                                withContext(Dispatchers.Main) {
                                                    saveFixturesToFirestore(teamId, fixtures)
                                                }
                                            } else {
                                                Log.e("API", "Error al obtener las fixtures del equipo: $teamId")
                                            }
                                        }
                                    } else {
                                        Log.e("TeamId", "El ID del equipo es null")
                                    }
                                }

                            } else{
                                Log.d("Firestore", "No se encontraron favoritos para el usuario: ${user.uid}")
                            }
                        }.addOnFailureListener { exception ->
                            Log.e("Firestore", "Error al obtener favoritos", exception)
                        }
                }else{
                    // El usuario no ha iniciado sesión
                    // Mostrar un pop up que mencione que deves crear una cuenta o iniciar sesión para agregar a favoritos
                }
            } else {
                showError()
                showOfflineLayout()
            }
        }

        registerReceiver(networkChangeReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(networkChangeReceiver)
    }

    private fun showOnlineLayout() {
        binding.layoutOnline.visibility = View.VISIBLE
        binding.layoutOffline.visibility = View.GONE
        Handler(Looper.getMainLooper()).postDelayed({
            binding.layoutOnline.visibility = View.GONE
        }, 3000)
    }

    private fun showOfflineLayout() {
        binding.layoutOnline.visibility = View.GONE
        binding.layoutOffline.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            binding.layoutOffline.visibility = View.GONE
        }, 5000)
    }

    private fun showError() {
        Toast.makeText(this, "Error: Sin conección a internet", Toast.LENGTH_SHORT).show()
    }

    private fun isOnline(): Boolean {
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://v3.football.api-sports.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private suspend fun getFixturesByTeam(teamId: String): List<fixtureResponse>? {
        val fixturesService = getRetrofit().create(FixturesByTeamService::class.java)
        val fixturesResponse = fixturesService.getFixtureByTeam(teamId.toInt(), 2023)
        return if (fixturesResponse.isSuccessful) {
            fixturesResponse.body()?.response
        } else {
            null
        }
    }

    private suspend fun getLineupsByFixture(fixtureId: String): LineupResponse? {
        val lineupService = getRetrofit().create(LineupService::class.java)
        val lineupResponse = lineupService.getLineup(fixtureId)
        return if (lineupResponse.isSuccessful) {
            lineupResponse.body()
        } else {
            null
        }
    }

    private fun saveFixturesToFirestore(teamId: String, fixtures: List<fixtureResponse>) {
        val db = FirebaseFirestore.getInstance()
        val teamDocument = db.collection("teams").document(teamId)
        val fixturesCollection = teamDocument.collection("fixtures")

        for (fixture in fixtures) {
            // Aquí asumimos que cada fixture tiene un id único
            val fixtureDocument = fixturesCollection.document(fixture.fixture.id.toString())
            Log.d("Firestore", "Guardando fixture con id ${fixture.fixture.id} para el equipo $teamId")
            fixtureDocument.set(fixture)
                .addOnSuccessListener {
                    Log.d("Firestore", "Fixture guardado con éxito")
                    CoroutineScope(Dispatchers.IO).launch {
                        val lineup = getLineupsByFixture(fixture.fixture.id.toString())
                        if (lineup != null) {
                            // Guardar los lineups en Firestore
                            withContext(Dispatchers.Main) {
                                saveLineupsToFirestore(fixture.fixture.id.toString(), lineup)
                            }
                        } else {
                            Log.e("API", "Error al obtener los lineups de la fixture: ${fixture.fixture.id}")
                        }
                    }

                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error al guardar fixture", e)
                }
        }
    }

    private fun saveLineupsToFirestore(fixtureId: String, lineup: LineupResponse) {
        if (lineup.response.isNotEmpty()) {
            val db = FirebaseFirestore.getInstance()
            val fixtureDocument = db.collection("fixtures").document(fixtureId)
            val lineupsCollection = fixtureDocument.collection("lineups")

            // Aquí asumimos que cada lineup tiene un id único
            val lineupDocument = lineupsCollection.document(lineup.response[0].team.id.toString())
            Log.d("Firestore", "Guardando lineup para la fixture $fixtureId")
            lineupDocument.set(lineup)
                .addOnSuccessListener {
                    Log.d("Firestore", "Lineup guardado con éxito")
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error al guardar lineup", e)
                }
        } else {
            Log.e("Firestore", "La respuesta de la alineación está vacía para la fixture: $fixtureId")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_navigation, menu)

        val search = menu?.findItem(R.id.svGeneral)
        val searchView = search?.actionView as SearchView

        /*val authItem = menu?.findItem(R.id.Auth)
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            val wrappedDrawable = authItem?.icon?.let { DrawableCompat.wrap(it) }
            wrappedDrawable?.let { DrawableCompat.setTint(it, ContextCompat.getColor(this, R.color.colorGreen)) }
            authItem?.icon = wrappedDrawable
        }*/
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    leaguesFragment?.adapter?.filter(query.toLowerCase())
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.Favorites ->{
                replaceFragment(Favorites())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private var leaguesFragment: Leagues? = null
    private fun replaceFragment(fragment: Fragment){
        if (fragment is Leagues) {
            leaguesFragment = fragment
        }

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragment)
        fragmentTransaction.commit()
    }


}
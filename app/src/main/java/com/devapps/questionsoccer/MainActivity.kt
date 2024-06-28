package com.devapps.questionsoccer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import com.devapps.questionsoccer.databinding.ActivityMainBinding
import com.devapps.questionsoccer.interfaces.FixturesByTeamService
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        Firebase.firestore.firestoreSettings = settings

        binding = ActivityMainBinding.inflate(layoutInflater)
        //enableEdgeToEdge()
        setContentView(binding.root)

        replaceFragment(Leagues())
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.leagues -> replaceFragment(Leagues())
                //R.id.teams -> replaceFragment(Teams())
                //R.id.players -> replaceFragment(Players())
                R.id.favorites -> replaceFragment(LiveFixtures())
                R.id.countries -> replaceFragment(Countries())
                //R.id.favorites_test -> replaceFragment(Favorites_Test())
                else ->{
                }
            }
            true
        }



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
                            val retrofit = getRetrofit()
                            Log.d("TeamId", "El ID del equipo es: ${teamId}")


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


    }
    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://v3.football.api-sports.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_navigation, menu)

        val search = menu?.findItem(R.id.svGeneral)
        val searchView = search?.actionView as SearchView

        val authItem = menu?.findItem(R.id.Auth)
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            val wrappedDrawable = authItem?.icon?.let { DrawableCompat.wrap(it) }
            wrappedDrawable?.let { DrawableCompat.setTint(it, ContextCompat.getColor(this, R.color.colorGreen)) }
            authItem?.icon = wrappedDrawable
        }
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
            R.id.Auth ->{
                navigateToAuthenticationActivity()
                true
            }
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


    private fun navigateToAuthenticationActivity(){
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
    }

}
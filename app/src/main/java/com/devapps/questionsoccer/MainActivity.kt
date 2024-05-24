package com.devapps.questionsoccer

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ActionMenuView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import com.devapps.questionsoccer.databinding.ActivityMainBinding
import com.google.android.play.integrity.internal.i
import com.google.firebase.auth.FirebaseAuth
import org.checkerframework.common.returnsreceiver.qual.This


enum class ProviderType{
    BASIC
}
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        //enableEdgeToEdge()
        setContentView(binding.root)
        val usernameTest = intent.getStringExtra("email")
        title = usernameTest ?: "QuestionSoccer"

        replaceFragment(Leagues())
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.leagues -> replaceFragment(Leagues())
                R.id.teams -> replaceFragment(Teams())
                R.id.players -> replaceFragment(Players())
                R.id.favorites -> replaceFragment(MyFavorites())
                R.id.countries -> replaceFragment(Countries())
                //R.id.favorites_test -> replaceFragment(Favorites_Test())
                else ->{
                }
            }
            true
        }

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
                    teamsFragment?.adapter?.filterTeams(query.toLowerCase())

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
                replaceFragment(Favorites_Test())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private var leaguesFragment: Leagues? = null
    private var teamsFragment: Teams? = null
    private fun replaceFragment(fragment: Fragment){
        if (fragment is Leagues) {
            leaguesFragment = fragment
        } else if( fragment is Teams){
            teamsFragment = fragment
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
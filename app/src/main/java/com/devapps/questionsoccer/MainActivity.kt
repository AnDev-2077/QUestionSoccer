package com.devapps.questionsoccer

import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.devapps.questionsoccer.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        //enableEdgeToEdge()
        setContentView(binding.root)
        replaceFragment(Leagues())

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.leagues -> replaceFragment(Leagues())
                R.id.teams -> replaceFragment(Teams())
                R.id.players -> replaceFragment(Players())
                R.id.favorites -> replaceFragment(MyFavorites())
                R.id.countries -> replaceFragment(Countries())

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
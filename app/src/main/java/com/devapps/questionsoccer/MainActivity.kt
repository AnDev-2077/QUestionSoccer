package com.devapps.questionsoccer

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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

                else ->{
                }
            }
            true
        }
        
    }
    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragment)
        fragmentTransaction.commit()
    }
}
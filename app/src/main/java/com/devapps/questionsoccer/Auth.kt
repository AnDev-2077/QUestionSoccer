package com.devapps.questionsoccer

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.devapps.questionsoccer.databinding.FragmentAuthBinding
import com.devapps.questionsoccer.databinding.ItemStandingBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.common.collect.ComparisonChain.start
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso


class Auth : Fragment() {


    private lateinit var binding: FragmentAuthBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            updateUI(user)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }
    private var clickCount = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = firebaseAuth.currentUser
        updateUI(user)

        binding.ivProfile.setOnClickListener{
            clickCount++
            Log.w("Egg", "Click count: $clickCount")
            if(clickCount == 10){
                binding.ivProfile.setImageResource(R.drawable.huu_cat)
                MediaPlayer.create(context, R.raw.huh_cat_sound).apply {
                    start()
                    setOnCompletionListener { mp -> mp.release() }
                }

                Handler(Looper.getMainLooper()).postDelayed({
                    MediaPlayer.create(context, R.raw.huu_cat_reverse).apply {
                        start()
                        setOnCompletionListener { mp -> mp.release() }
                    }
                }, 2617)

                Handler(Looper.getMainLooper()).postDelayed({
                    binding.ivProfile.setImageResource(R.drawable.ic_person_circle)
                    clickCount = 0
                }, 3000)
            }
            if(clickCount == 20){
                binding.ivProfile.setImageResource(R.drawable.internet)
            }
        }


        binding.btStart.setOnClickListener{
            val intent = Intent(activity, AuthActivity::class.java)
            startActivity(intent)
        }

        binding.btEnd.setOnClickListener{
            logoutConfirmationDialog()
        }
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(authStateListener)
    }

    private fun updateUI(user: FirebaseUser?) {
        user?.let {
            binding.btStart.visibility = View.GONE
            binding.btEnd.visibility = View.VISIBLE

            val name = user.displayName
            val photoUrl = user.photoUrl
            binding.tvWelcome.text = "Hola $name"
            Picasso.get().load(photoUrl).into(binding.ivProfile)
        } ?: run {
            binding.btStart.visibility = View.VISIBLE
            binding.btEnd.visibility = View.GONE
            binding.tvWelcome.text = "INICIA SESIÓN!"
            binding.ivProfile.setImageResource(R.drawable.ic_person_circle)
        }
    }

    private fun logoutConfirmationDialog(){
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro de que deseas cerrar sesión?")
            .setNegativeButton("Cancelar") { dialog, which ->

            }
            .setPositiveButton("Cerrar Sesión") { dialog, which ->
                firebaseAuth.signOut()
            }
            .show()
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            Auth().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
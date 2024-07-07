package com.devapps.questionsoccer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.devapps.questionsoccer.databinding.FragmentAuthBinding
import com.devapps.questionsoccer.databinding.ItemStandingBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = firebaseAuth.currentUser
        updateUI(user)

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
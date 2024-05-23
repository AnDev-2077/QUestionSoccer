package com.devapps.questionsoccer

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.devapps.questionsoccer.databinding.ActivityAuthBinding
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUp()
    }
     private fun setUp (){
         title = "Autenticación"
         binding.btSingUp.setOnClickListener {
             if (binding.etEmailAddress.text.isNotEmpty() && binding.etPassword.text.isNotEmpty()){
                 FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.etEmailAddress.text.toString(),
                     binding.etPassword.text.toString()).addOnCompleteListener{
                         if (it.isSuccessful){
                             val user = FirebaseAuth.getInstance().currentUser
                             val photoUrl = user?.photoUrl
                             showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                         }else{
                             showAlert()
                         }
                 }

             }
         }
         binding.btLogIn.setOnClickListener {
             if (binding.etEmailAddress.text.isNotEmpty() && binding.etPassword.text.isNotEmpty()){
                 FirebaseAuth.getInstance().signInWithEmailAndPassword(binding.etEmailAddress.text.toString(),
                     binding.etPassword.text.toString()).addOnCompleteListener{
                     if (it.isSuccessful){
                         val user = FirebaseAuth.getInstance().currentUser
                         val photoUrl = user?.photoUrl
                         showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                     }else{
                         showAlert()
                     }
                 }

             }
         }
     }
    private fun showAlert(){
        val builder = AlertDialog.Builder( this)
        builder.setTitle("Error")
        builder. setMessage("Se ha producido un error autenticando al usuario")
        builder. setPositiveButton ("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog. show()
    }

    private fun showHome(email: String, provider: ProviderType){
        val homeIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }

}
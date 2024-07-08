package com.devapps.questionsoccer

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.devapps.questionsoccer.databinding.ActivityAbouUsBinding
import com.devapps.questionsoccer.databinding.FragmentAuthBinding

class AbouUsActivity : AppCompatActivity() {

    lateinit var binding: ActivityAbouUsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_abou_us)
        binding = ActivityAbouUsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.emailButton.setOnClickListener{
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:carlosipaca2003@gmail.com")
            }
            try {
                startActivity(emailIntent)
            } catch (e: ActivityNotFoundException) {

            }
        }

        binding.visitSite.setOnClickListener{
            val url = "https://668b8db2f28330cfc64ba89a--kaleidoscopic-eclair-e478a3.netlify.app/"
            val webIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
            }
            try {
                startActivity(webIntent)
            } catch (e: ActivityNotFoundException) {

            }
        }
    }
}
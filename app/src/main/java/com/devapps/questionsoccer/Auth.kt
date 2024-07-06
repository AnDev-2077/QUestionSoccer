package com.devapps.questionsoccer

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.devapps.questionsoccer.databinding.FragmentAuthBinding
import com.devapps.questionsoccer.databinding.ItemStandingBinding


class Auth : Fragment() {


    private lateinit var binding: FragmentAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

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
        binding.btStart.setOnClickListener{
            val intent = Intent(activity, AuthActivity::class.java)
            startActivity(intent)
        }
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
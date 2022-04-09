package com.example.testproject.game.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.Toast
import com.example.testproject.R
import com.example.testproject.databinding.FragmentSettingsBinding
import com.example.testproject.game.fragments.utils.Constants.APP
import com.example.testproject.game.fragments.utils.Variables.engorru
import com.example.testproject.game.fragments.utils.Variables.exit
import com.example.testproject.game.fragments.utils.Variables.retry

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.switch1.setOnCheckedChangeListener({ _, isChecked ->
            engorru = if (isChecked) true else false
        })

        binding.switch2.setOnCheckedChangeListener({ _, isChecked ->
            exit = if (isChecked) true else false
        })

        binding.switch3.setOnCheckedChangeListener({ _, isChecked ->
            retry = if (isChecked) true else false
        })

    }

}
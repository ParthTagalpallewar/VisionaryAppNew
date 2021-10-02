package com.reselling.visionary.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.reselling.visionary.R
import com.reselling.visionary.databinding.ActivityAuthBinding
import com.reselling.visionary.utils.changeStatusBarColor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
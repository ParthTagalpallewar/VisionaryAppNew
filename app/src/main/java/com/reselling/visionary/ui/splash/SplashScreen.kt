package com.reselling.visionary.ui.splash

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.reselling.visionary.R
import com.reselling.visionary.databinding.FragmentSplashBinding
import com.reselling.visionary.ui.MainActivity
import com.reselling.visionary.ui.auth.AuthActivity
import com.reselling.visionary.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

private const val TAG = "SplashScreen"

@AndroidEntryPoint
class SplashScreen : AppCompatActivity() {

    private val viewModel: NewSplashViewModel by viewModels()
    lateinit var binding: FragmentSplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentSplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.changeStatusBarColor(R.color.white)



        binding.animationView.setUpAnimation()

        lifecycleScope.launchWhenStarted {
            viewModel.tasksEvent.collect { event ->


                binding.animationView.isVisible = event is NewSplashViewModel.SplashEvents.Loading

                when (event) {
                    is NewSplashViewModel.SplashEvents.NavigateToLoginScreen -> {
                        this@SplashScreen.move(AuthActivity::class.java)

                    }
                    is NewSplashViewModel.SplashEvents.NavigateToHomeScreen -> {
                        this@SplashScreen.move(MainActivity::class.java)
                    }

                    is NewSplashViewModel.SplashEvents.InternetProblem -> {
                        binding.root.snackBar(internetExceptionString, "Turn On") { snackBar ->
                            try {
                                startActivity(Intent(Settings.ACTION_DATA_ROAMING_SETTINGS))
                                snackBar.build().dismiss()
                            } catch (e: Exception) {
                            }

                        }
                    }


                    is NewSplashViewModel.SplashEvents.ShowInvalidInputMessage -> {
                        binding.root.snackBar(event.msg, "Ok") { snackBar ->
                            snackBar.build().dismiss()
                        }

                    }


                }
            }
        }

    }

}
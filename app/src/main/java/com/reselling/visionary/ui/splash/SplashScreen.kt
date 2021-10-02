package com.reselling.visionary.ui.splash

import android.os.Bundle
import android.util.Log
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

    /*Initializing view model using property delegate*/
    private val viewModel: SplashViewModel by viewModels()
    lateinit var binding: FragmentSplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentSplashBinding.inflate(layoutInflater)


        binding.apply {
            setContentView(root) // setting content view
            changeStatusBarColor(R.color.white ) //changing status bar color to white
            animationView.setUpAnimation() // starting animation of splash screen
        }


        lifecycleScope.launchWhenStarted {
            //collecting all events from viewModel
            viewModel.splashEventsFlow.collect { event ->

                // if event is loading then only show animation on splash screen
                binding.animationView.isVisible = event is SplashEvents.LoadingEvent

                when (event) {
                    is SplashEvents.NavigateToLoginScreen -> {
                        this@SplashScreen.move(AuthActivity::class.java)
                    }
                    is SplashEvents.NavigateToHomeScreen -> {
                        this@SplashScreen.move(MainActivity::class.java)
                    }

                    is SplashEvents.LoadingEvent -> {
                        // this will call with there is loading event
                        Log.e(TAG, "onCreate: Loading in Splash Screen")
                    }
                }
            }
        }

    }

}
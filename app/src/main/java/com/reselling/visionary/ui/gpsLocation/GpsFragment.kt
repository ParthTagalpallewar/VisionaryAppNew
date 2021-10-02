package com.reselling.visionary.ui.gpsLocation

import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.reselling.visionary.R
import com.reselling.visionary.databinding.FragmentLocationGpsBinding
import com.reselling.visionary.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

//private const val TAG = "GpsFragment"

@AndroidEntryPoint
class GpsFragment : Fragment(R.layout.fragment_location_gps) {

    private val viewModel: GpsFragmentViewModel by activityViewModels()
    private lateinit var binding: FragmentLocationGpsBinding

    @Inject lateinit var locationManager : LocationManager

    override fun onStart() {
        super.onStart()
        if (!locationManager.isProviderEnabled(Constants.LocationProviderConstant)){
            requireContext().setUpLocationAlertDialogue()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLocationGpsBinding.bind(view)


        val requestMultiplePermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            viewModel.handelPermissionResult()
        }

        binding.apply {

            btnAroundMe.setOnClickListener {
                 viewModel.makeLocationCall()
            }

            setLocationManually.setOnClickListener {
                GpsFragmentDirections.actionGpsFragmentToManualLocation().also {
                    findNavController().navigate(it)
                }
            }

        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.gpsEvent.collect { it ->

                binding.cardViewLoading.isVisible =
                        it is GpsFragmentViewModel.GpsLocationEvents.LoadingEvent
                when (it) {

                    is GpsFragmentViewModel.GpsLocationEvents.LoadingEvent -> {
                        binding.animationView.setUpAnimation()
                    }

                    is GpsFragmentViewModel.GpsLocationEvents.ShowInvalidInputMessage -> {
                        requireView().snackBar(it.msg)
                    }

                    is GpsFragmentViewModel.GpsLocationEvents.NavigateToMainFragment -> {
                        GpsFragmentDirections.actionGpsFragmentToBaseHomeFragment2().also {
                            findNavController().navigate(it)
                        }

                    }

                    is GpsFragmentViewModel.GpsLocationEvents.RequestPermission -> {
                        requestMultiplePermissions.launch(it.permissions)
                    }

                    is GpsFragmentViewModel.GpsLocationEvents.ReReqPermission -> {
                        requireView().snackBar("Please Allow Permissions To Get Your Location", "Ok") {
                            try {
                                viewModel.makeLocationCall()
                            } catch (e: Exception) {
                            }

                        }
                    }

                    is GpsFragmentViewModel.GpsLocationEvents.InternetProblem -> {
//                        Log.e(TAG, "onViewCreated: Internet Problem")
                        requireView().snackBar(internetExceptionString, "Ok") { snackBar ->
                            startActivity(Intent(Settings.ACTION_DATA_ROAMING_SETTINGS))
                            snackBar.build().dismiss()
                        }
                    }

                    is GpsFragmentViewModel.GpsLocationEvents.RequestGpsEnable -> {
                        viewLifecycleOwner.lifecycleScope.launch {
                            requireContext().setUpLocationAlertDialogue()
                        }

                    }

                    is GpsFragmentViewModel.GpsLocationEvents.ObserveLiveDataUpdateResponse -> {
                        viewModel.handelResponseOnUpdatedLocation(it.response)
                    }

                }
            }
        }

    }


}


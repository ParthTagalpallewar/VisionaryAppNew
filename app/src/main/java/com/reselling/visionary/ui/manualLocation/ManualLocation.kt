package com.reselling.visionary.ui.manualLocation

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.reselling.visionary.R
import com.reselling.visionary.data.models.manualLocation.ManualLocation
import com.reselling.visionary.databinding.FragmentLocationPincodeBinding
import com.reselling.visionary.utils.hideKeyboard
import com.reselling.visionary.utils.internetExceptionString
import com.reselling.visionary.utils.snackBar
import kotlinx.coroutines.flow.collect

class ManualLocation() : Fragment(R.layout.fragment_location_pincode),
    PincodeLocationAdapter.OnItemClickListener {

    private val viewModel: ManualLocationViewModel by activityViewModels()
    private lateinit var binding: FragmentLocationPincodeBinding
    private lateinit var pinCodeCityAdapter: PincodeLocationAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLocationPincodeBinding.bind(view)

        binding.apply {
            cancelBtn.setOnClickListener {
                findNavController().popBackStack()
            }

            pinCodeSearchBtn.setOnClickListener {

                viewModel.getCitiesByPincode()
            }

            pinCodeEditText.addTextChangedListener {
                viewModel.pinCode = it.toString()
            }

            cancelBtn.setOnClickListener {
                findNavController().navigate(R.id.gpsFragment,null,
                        NavOptions.Builder().setPopUpTo(R.id.manualLocation, true).build())

            }


        }

        setUpRecycerView()

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            viewModel.manualLocationEvents.collect {

                binding.progressBar.isVisible =
                    (it == ManualLocationViewModel.ManualLocationEvents.LoadingEvent)

                when (it) {
                    is ManualLocationViewModel.ManualLocationEvents.ShowInvalidInputMessage -> {
                        requireView().snackBar(it.msg)
                    }

                    is ManualLocationViewModel.ManualLocationEvents.ObserveManualCityResponse -> {
                        viewModel.handleCitesResponse(it.cityList)
                    }

                    is ManualLocationViewModel.ManualLocationEvents.InternetProblem -> {
                        requireView().snackBar(internetExceptionString, "Ok") { snackBar ->
                            try {
                                startActivity(Intent(Settings.ACTION_DATA_ROAMING_SETTINGS))
                                snackBar.build().dismiss()
                            } catch (e: Exception) {
                            }
                        }
                    }

                    is ManualLocationViewModel.ManualLocationEvents.ShowCitiesInRecyclerView -> {

                        pinCodeCityAdapter.apply {
                            requireActivity().hideKeyboard()
                            submitList(it.cities)
                            notifyDataSetChanged()
                        }
                    }


                    is ManualLocationViewModel.ManualLocationEvents.NavigateToMainFragment -> {

                        ManualLocationDirections.actionManualLocationToBaseHomeFragment2().apply {
                            findNavController().navigate(this)

                        }

                    }

                    is ManualLocationViewModel.ManualLocationEvents.ObserveUpdateLocationResponse -> {

                        viewModel.handelResponseOnUpdatedLocation(it.updateResponse)
                    }


                }
            }
        }


    }

    private fun setUpRecycerView() {
        pinCodeCityAdapter = PincodeLocationAdapter(this)
        binding.recyclerView.apply {
            adapter = pinCodeCityAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }

    override fun onItemClick(loc: ManualLocation) {
        viewModel.handelRecyclerCityItemClick(loc)
    }

}
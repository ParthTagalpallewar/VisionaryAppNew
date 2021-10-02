package com.reselling.visionary.ui.home.homeBookDetails.sellerDetails

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.reselling.visionary.R
import com.reselling.visionary.ui.home.homeBookDetails.HomeBookDetailsViewModel

class MapsFragment : Fragment(R.layout.fragment_seller_details_maps), OnMapReadyCallback {


    val viewModel: HomeBookDetailsViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.booDetailFragmentMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap?) {
        viewModel.books.observe(viewLifecycleOwner) {
            val locations = it.location.split(",")

            val lat = locations[0].toDouble()
            val long = locations[1].toDouble()


            val sellerLatLong = LatLng(lat, long)
            val zoomLevel = 16.0f
            map!!.apply {
                addMarker(MarkerOptions().position(sellerLatLong).title(it.bookName))
                //This goes up to 21
                moveCamera(CameraUpdateFactory.newLatLngZoom(sellerLatLong,zoomLevel))
            }

        }
    }

}
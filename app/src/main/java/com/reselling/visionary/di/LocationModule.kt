package com.reselling.visionary.di

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.location.Geocoder
import android.location.LocationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.*

@InstallIn(SingletonComponent::class)
@Module
object LocationModule {


    @Provides
    fun provideFusedLocationProvider(app: Application): FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(app)

    @Provides
    fun provideCancellationTokenSource(): CancellationTokenSource = CancellationTokenSource()


    @Provides
    fun provideGeoCoder(@ApplicationContext context: Context): Geocoder = Geocoder(context, Locale.getDefault())


    @Provides
    fun provideLocationManager(@ApplicationContext context: Context): LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

}
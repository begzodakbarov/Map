package com.example.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.map.databinding.ActivityMapsBinding
import com.github.florent37.runtimepermission.RuntimePermission.askPermission
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.tasks.Task

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID

        myAskPermission()
    }
    fun myAskPermission() {
        askPermission(Manifest.permission.ACCESS_FINE_LOCATION) {
            //all permissions already granted or just granted
            findLocation()
        }.onDeclined { e ->
            if (e.hasDenied()) {

                AlertDialog.Builder(this)
                    .setMessage("Please accept our permissions")
                    .setPositiveButton("yes") { dialog, which ->
                        e.askAgain();
                    } //ask again
                    .setNegativeButton("no") { dialog, which ->
                        dialog.dismiss();
                    }
                    .show();
            }

            if (e.hasForeverDenied()) {
                e.goToSettings();
            }
        }

    }

    @SuppressLint("MissingPermission")
    fun findLocation() {
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)
        val locationTask: Task<Location> = fusedLocationProviderClient.lastLocation
        locationTask.addOnSuccessListener { it: Location ->
            //We have a location
            Log.d(TAG, "getLastLocation: ${it.toString()}")
            Log.d(TAG, "getLastLocation: ${it.latitude}")
            Log.d(TAG, "getLastLocation: ${it.longitude}")
            val codial = LatLng(it.latitude, it.longitude)

            val marker = mMap.addMarker(MarkerOptions().position(codial).title("Bizning Joylashuv"))
            val cameraPosition = CameraPosition.builder()
                .target(codial)
                .zoom(18f)
                .bearing(0f)
                .tilt(50f)
                .build()
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
        locationTask.addOnFailureListener {
            Log.d(TAG, "getLastLocation: ${it.message}")
        }
    }
}
package com.example.loca



import android.content.Intent
import android.content.pm.PackageManager
import android.health.connect.datatypes.ExerciseRoute
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.loca.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.Marker
import java.util.Locale

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    var currentMarker:Marker?=null
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    var fusedlocationProviderClient : FusedLocationProviderClient?=null
    var currentLocation: Location?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userEmail=intent.getStringExtra("USER_EMAIL");
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        val mapFragment = supportFragmentManager
//            .findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this)
        fusedlocationProviderClient= LocationServices.getFusedLocationProviderClient(this)
        fetchLocation()
    }

    private fun fetchLocation() {
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)
            !=PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)
            !=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf( android.Manifest.permission.ACCESS_FINE_LOCATION),1000)
            return
        }
        val task= fusedlocationProviderClient?.lastLocation

        task?.addOnSuccessListener {location->
            if(location!=null){
                this.currentLocation=location
                val mapFragment = supportFragmentManager
                    .findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this)
            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            1000-> if(grantResults.size>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                fetchLocation()
            }
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val latlng = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
        drawMarker(latlng)

        mMap.setOnMarkerDragListener(object :GoogleMap.OnMarkerDragListener{
            override fun onMarkerDrag(p0: Marker) {
                TODO("Not yet implemented")
            }

            override fun onMarkerDragEnd(p0: Marker) {
                if(currentMarker!=null)
                    currentMarker?.remove()
                val newLatLng=LatLng(p0?.position!!.latitude,p0?.position!!.longitude)
                drawMarker(newLatLng)
            }

            override fun onMarkerDragStart(p0: Marker) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun drawMarker(latLng: LatLng) {
        // Clear existing markers if needed
        mMap.clear()
        val markerOptions=MarkerOptions().position(latLng).title("Marker Title").snippet(getTheaddress(latLng.latitude,latLng.longitude)).draggable(true)

        // Move the camera to the marker position with an appropriate zoom level
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        currentMarker=mMap.addMarker(markerOptions)
        currentMarker?.showInfoWindow()
        val address=getTheaddress(latLng.latitude,latLng.longitude)
        val userEmail=intent.getStringExtra("USER_EMAIL");
        Toast.makeText(this@MapsActivity,"Fetching Live Location ...",Toast.LENGTH_SHORT).show()
        // Delay the intent by 5 seconds
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            val intent = Intent(this, Accident::class.java)
            intent.putExtra("USER_EMAIL",userEmail)
            intent.putExtra("ADDRESS",address)
            startActivity(intent)
            finish()
        }, 5000) // 5000 milliseconds (5 seconds)
    }


    private fun getTheaddress(lat: Double, lon: Double):String?{
        val geoCoder= Geocoder(this, Locale.getDefault())
        val addresses= geoCoder.getFromLocation(lat,lon,1)
        val pick =addresses?.get(0)?.getAddressLine(0).toString()

        return pick

    }
}

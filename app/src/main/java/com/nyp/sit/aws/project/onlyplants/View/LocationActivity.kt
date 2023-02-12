package com.nyp.sit.aws.project.onlyplants.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView

import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.geo.location.AWSLocationGeoPlugin
import com.amplifyframework.geo.location.models.AmazonLocationPlace
import com.amplifyframework.geo.maplibre.view.MapLibreView
import com.amplifyframework.geo.maplibre.view.support.fadeIn
import com.amplifyframework.geo.maplibre.view.support.fadeOut
import com.amplifyframework.geo.models.Coordinates
import com.amplifyframework.geo.options.GeoSearchByCoordinatesOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.nyp.sit.aws.project.onlyplants.MainActivity
import com.nyp.sit.aws.project.onlyplants.R

//import com.amazonaws.services.location.model.*
//import com.amazonaws.mobileconnectors.geo.tracker.* // Required for Amazon Location Service
//import com.amazonaws.mobileconnectors.geo.tracker.publisher.* // Required for Amazon Location Service
//import com.amazonaws.mobileconnectors.geo.tracker.service.* // Required for Amazon Location Service
//import com.amazonaws.services.geo.model.* // Required for Amazon Location Service
//import com.amplifyframework.core.model.temporal.Temporal.DateTime
//import com.amplifyframework.geo.AWSLocationProvider
//import com.amplifyframework.geo.models.*
//import java.util.*


class LocationActivity : AppCompatActivity() {

    private val mapView by lazy {
        findViewById<MapLibreView>(R.id.mapView)
    }

    private val descriptionView by lazy {
        findViewById<TextView>(R.id.description_text_view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAmplify()
        setContentView(R.layout.activity_location_service)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_home)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mapView.getMapAsync { map ->
            val initialPosition = LatLng(1.3800, 103.8489)
            map.cameraPosition = CameraPosition.Builder()
                .target(initialPosition)
                .zoom(13.0)
                .build()
            map.addOnCameraMoveStartedListener { toggleDescriptionText() }
            map.addOnCameraIdleListener { reverseGeocode(map) }
    }
}

//    private fun forwardGeocode(queryText: String) {
//        val request = SearchPlaceIndexForTextRequest.builder()
//            .text(queryText)
//            .maxResults(1)
//            .build()
//
//        Amplify.Location.searchPlaceIndexForText(request,
//            { result ->
//                result.places.firstOrNull()?.let { place ->
//                    val amazonPlace = (place as AmazonLocationPlace)
//                    val location = amazonPlace.geometry.point
//                    runOnUiThread { toggleDescriptionText("Latitude: ${location[1]}, Longitude: ${location[0]}") }
//                }
//            },
//            { exp ->
//                Log.e("AndroidQuickStart", "Failed to geocode : $exp")
//            }
//        )
//    }

    private fun reverseGeocode(map: MapboxMap) {
        val options = GeoSearchByCoordinatesOptions.builder()
            .maxResults(1)
            .build()

        val centerCoordinates = Coordinates().apply {
            longitude = map.cameraPosition.target.longitude
            latitude = map.cameraPosition.target.latitude
        }
        Amplify.Geo.searchByCoordinates(centerCoordinates, options,
            { result ->
                result.places.firstOrNull()?.let { place ->
                    val amazonPlace = (place as AmazonLocationPlace)
                    runOnUiThread { toggleDescriptionText(amazonPlace.label) }
                }
            },
            { exp ->
                Log.e("AndroidQuickStart", "Failed to reverse geocode : $exp")
            }
        )
    }

    private fun toggleDescriptionText(label: String? = "") {
        if (label.isNullOrBlank()) {
            descriptionView.fadeOut()
        } else {
            descriptionView.text = label
            descriptionView.fadeIn()
        }
    }
    private fun initAmplify() {
        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.addPlugin(AWSLocationGeoPlugin())
            Amplify.configure(applicationContext)
            Log.i("AndroidQuickStart", "Initialized Amplify")
        } catch (error: AmplifyException) {
            Log.e("AndroidQuickStart", "Could not initialize Amplify", error)
        }
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
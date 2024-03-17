package com.example.travel_app

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.example.travel_app.BuildConfig
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.model.AddressComponent
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest

class TestAPIFragment : Fragment() {

    private val TAG = "TestAPIFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test_a_p_i, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Define a variable to hold the Places API key.
        val apiKey = BuildConfig.PLACES_API_KEY

        Log.e("keykeykey", apiKey)

        // Log an error if apiKey is not set.
        if (apiKey.isEmpty() || apiKey == "DEFAULT_API_KEY") {
            Log.e("Places test", "No api key")
            activity?.finish()
            return
        }

        // Initialize the SDK
        Places.initialize(requireContext(), apiKey)

        // Create a new PlacesClient instance
        val placesClient = Places.createClient(requireActivity())

        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                    as AutocompleteSupportFragment

        autocompleteFragment.setHint("장소 검색")

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))

        val fields = listOf(Place.Field.ID, Place.Field.NAME)

        val autocompleteRequest = FindAutocompletePredictionsRequest.builder()
            .setSessionToken(AutocompleteSessionToken.newInstance())
            .setQuery("검색어")
            .build()

        placesClient.findAutocompletePredictions(autocompleteRequest).addOnSuccessListener { response ->
            for (prediction in response.autocompletePredictions.take(10)) { // 최대 5개의 결과만 가져옵니다.
                Log.i(TAG, "Place prediction: ${prediction.getFullText(null)}")
            }
        }.addOnFailureListener { exception ->
            if (exception is ApiException) {
                Log.e(TAG, "Place not found: ${exception.statusCode}: ${exception.message}")
            }
        }

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: ${place.name}, ${place.id}")

                // Get info about the selected place.
                val placeName = place.name
                val placeId = place.id
                val placeAddress = place.address // 장소의 주소

                // Check if place has categories
                val placeTypes = place.placeTypes // 장소의 카테고리 리스트
                val placeCategories = mutableListOf<String>()
                placeTypes?.forEach { type ->
                    val category = type.toUpperCase() // 카테고리 이름을 대문자로 변환
                    placeCategories.add(category)
                }
                Log.i(TAG, "region: $placeTypes")
                // Log the retrieved inf
                // Log the retrieved information
                Log.i(TAG, "Place: $placeName, $placeId, Address: $placeAddress")
                Log.i(TAG, "Categories: $placeCategories")
//                Log.i(TAG, "Locality: $placeLocality")
            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: $status")
            }
        })
    }
}
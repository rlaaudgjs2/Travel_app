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
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.libraries.places.api.model.AddressComponent
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient

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

                fetchPlaceDetails(place.id, placesClient)

            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: $status")
            }
        })
    }

    fun fetchPlaceDetails(placeId: String, placesClient: PlacesClient) {
        // Place Details 요청을 생성
        val placeRequest = FetchPlaceRequest.builder(placeId, listOf(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.TYPES))
            .build()

        // Place Details 요청을 보냄
        placesClient.fetchPlace(placeRequest)
            .addOnSuccessListener(OnSuccessListener { response ->
                val place = response.place
                // 성공적으로 장소 정보를 가져온 경우
                val placeName = place.name
                val placeAddress = place.address
                val placeTypes = place.placeTypes
                // 장소 정보를 처리하거나 출력
                // 예를 들어, Log에 출력
                Log.i(TAG, "Place Details - Name: $placeName, Address: $placeAddress, Types: $placeTypes")
            })
            .addOnFailureListener(OnFailureListener { exception ->
                // 장소 정보를 가져오는 데 실패한 경우
                Log.e(TAG, "Place Details request failed: ${exception.message}")
            })
    }
}
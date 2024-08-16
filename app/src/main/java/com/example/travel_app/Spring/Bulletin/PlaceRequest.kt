package com.example.travel_app.Spring.Bulletin

import android.os.Parcel
import android.os.Parcelable

data class PlaceRequest(
    val placeName: String
) : Parcelable {
    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        TODO("Not yet implemented")
    }
}
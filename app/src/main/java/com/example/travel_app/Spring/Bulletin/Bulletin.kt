package com.example.travel_app.Spring.Bulletin

import android.os.Parcel
import android.os.Parcelable
import java.util.Date

data class Bulletin(
    val bulletinId: Long,
    val creationDate: String,
    val likes: Int,
    val title: String,
    val authorId: Long
) : Parcelable {
    constructor(parcel: Parcel) : this(
        bulletinId = parcel.readLong(),
        creationDate = parcel.readString() ?: "",
        likes = parcel.readInt(),
        title = parcel.readString() ?: "",
        authorId = parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(bulletinId)
        parcel.writeString(creationDate) // Date를 long으로 변환하여 저장
        parcel.writeInt(likes)
        parcel.writeString(title)
        parcel.writeLong(authorId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Bulletin> {
        override fun createFromParcel(parcel: Parcel): Bulletin {
            return Bulletin(parcel)
        }

        override fun newArray(size: Int): Array<Bulletin?> {
            return arrayOfNulls(size)
        }
    }
}


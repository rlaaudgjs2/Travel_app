import android.os.Parcel
import android.os.Parcelable

data class Plan(
    val planId: Long,
    val region: String,
    val startDay: String,
    val endDay: String,
    val days: List<DayPlan>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createTypedArrayList(DayPlan.CREATOR) ?: emptyList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(planId)
        parcel.writeString(region)
        parcel.writeString(startDay)
        parcel.writeString(endDay)
        parcel.writeTypedList(days)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Plan> {
        override fun createFromParcel(parcel: Parcel): Plan = Plan(parcel)
        override fun newArray(size: Int): Array<Plan?> = arrayOfNulls(size)
    }
}

data class DayPlan(
    val dayNumber: Int,
    val places: MutableList<PlaceDetails>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.createTypedArrayList(PlaceDetails.CREATOR) ?: mutableListOf()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(dayNumber)
        parcel.writeTypedList(places)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<DayPlan> {
        override fun createFromParcel(parcel: Parcel): DayPlan = DayPlan(parcel)
        override fun newArray(size: Int): Array<DayPlan?> = arrayOfNulls(size)
    }
}

data class PlaceDetails(
    val name: String,
    val category: String,
    val photoUrl: String,
    val address: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(category)
        parcel.writeString(photoUrl)
        parcel.writeString(address)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<PlaceDetails> {
        override fun createFromParcel(parcel: Parcel): PlaceDetails = PlaceDetails(parcel)
        override fun newArray(size: Int): Array<PlaceDetails?> = arrayOfNulls(size)
    }
}


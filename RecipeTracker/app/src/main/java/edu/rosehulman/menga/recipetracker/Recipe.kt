package edu.rosehulman.menga.recipetracker

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp

data class Recipe(var title: String,
                  var ingredients: ArrayList<String>,
                  var instructions: String):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readArrayList(null) as ArrayList<String>,
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(instructions)
    }

    override fun describeContents(): Int {
        return 0
    }

    @get:Exclude
    var id =""
    @ServerTimestamp
    var creation: Timestamp? = null

    companion object CREATOR : Parcelable.Creator<Recipe> {
        const val CREATION_KEY = "creation"
        fun fromSnapshot(snapshot: DocumentSnapshot):Recipe{
            val pic = snapshot.toObject(Recipe::class.java)!!
            pic.id = snapshot.id
            return pic
        }

        override fun createFromParcel(source: Parcel): Recipe {
            return Recipe(source)
        }

        override fun newArray(size: Int): Array<Recipe?> {
            return arrayOfNulls(size)
        }
    }
}
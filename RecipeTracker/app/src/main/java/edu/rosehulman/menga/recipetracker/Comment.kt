package edu.rosehulman.menga.recipetracker

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Comment(var content: String?, val user: String?,val uid:String?):Parcelable {
}
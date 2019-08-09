package edu.rosehulman.menga.recipetracker

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Recipe(var title: String = "",
                  var ingredients: ArrayList<String> = ArrayList(),
                  var instructions: String = "", val uid: String = "",
                  var favoriteOf: String = "", var picId: Long = -1,
                  var url: String = ""): Parcelable {

    @get:Exclude
    var id =""
    @ServerTimestamp
    var creation: Timestamp? = null

    fun clone(): Recipe {
        return Recipe(title, ingredients, instructions, uid)
    }

    fun equals(other: Recipe): Boolean {
        return title==other.title && containsSame(ingredients, other.ingredients)
                && instructions == other.instructions
    }

    fun containsSame(l1: ArrayList<String>, l2: ArrayList<String>): Boolean {
        if(l1.size!=l2.size) return false
        for((i,j) in l1.withIndex()) {
            if(l1[i] != l2[i]) return false
        }
        return true
    }

    companion object {
        const val CREATION_KEY = "creation"
        fun fromSnapshot(snapshot: DocumentSnapshot): Recipe {
            val pic = snapshot.toObject(Recipe::class.java)!!
            pic.id = snapshot.id
            return pic
        }
    }
}
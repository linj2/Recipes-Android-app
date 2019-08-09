package edu.rosehulman.menga.recipetracker

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dialog_edit_recipe.view.*
import kotlinx.android.synthetic.main.recipe_view.view.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

private const val RC_TAKE_PICTURE = 1
private const val RC_CHOOSE_PICTURE = 2

class RecipeFragment: Fragment() {
    var recipe: Recipe? = null
    var previous: String? = null
    var viewedBy: String? = null
    val storageRef = FirebaseStorage.getInstance().reference.child(Constants.IMAGES_PATH)

    private var currentPhotoPath = ""
    var url = ""
    var into: ImageView? = null
    private var picId: Long = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {
            recipe = it?.getParcelable(Constants.ARG_RECIPE)
            previous = it?.getString(Constants.ARG_PREVIOUS)
            viewedBy = it?.getString(Constants.VIEWED_BY)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.recipe_view, container, false)
        val layout = view.findViewById<RelativeLayout>(R.id.holder_buttons)
        if (previous == Constants.SEARCH && viewedBy != recipe?.uid) {
            layout.removeView(view.findViewById(R.id.button_delete))
        }
        view.recipe_view_title.text = recipe?.title
        view.ingredients_view.text = recipe?.ingredients.toString()
        view.instructions_view.text = recipe?.instructions
        if(recipe?.picId != (-1).toLong()) {
            Picasso.get()
                .load(recipe?.url)
                .into(view.recipe_image_view)
        }
        if (previous != Constants.SEARCH || viewedBy == recipe?.uid) {
            view.button_delete.setOnLongClickListener {
                if (viewedBy != recipe?.uid && previous != Constants.FAVORITE) {
                    Toast.makeText(context, "You can't delete others' recipes!", Toast.LENGTH_SHORT).show()
                } else if(viewedBy != recipe?.uid && previous == Constants.FAVORITE) {
                    val builder = AlertDialog.Builder(context!!)
                    builder
                        .setMessage("Are you sure you want to delete this recipe?")
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            FirebaseFirestore.getInstance().collection(Constants.USERS_PATH).document(recipe!!.id).delete()
                            fragmentManager?.popBackStackImmediate()
                        }
                        .setNegativeButton(android.R.string.no, null)
                    builder.create().show()
                }
                else {
                    val builder = AlertDialog.Builder(context!!)
                    builder
                        .setMessage("Are you sure you want to delete this recipe?")
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            FirebaseFirestore.getInstance().collection(Constants.RECIPES_PATH).document(recipe!!.id)
                                .delete()
                            storageRef.child(recipe?.picId.toString()).delete()
                            fragmentManager?.popBackStackImmediate()
                        }
                        .setNegativeButton(android.R.string.no, null)
                    builder.create().show()
                }
                true
            }
        }
        view.button_return.setOnClickListener {
            Log.d(Constants.TAG, "back to $previous")
            when (previous) {
                Constants.MY_RECIPES -> {
                    val switchTo = MeFragment.newInstance(viewedBy!!)
                    val ft = activity!!.supportFragmentManager.beginTransaction()
                    ft.replace(R.id.fragment_container, switchTo)
                    activity!!.supportFragmentManager.popBackStackImmediate()
//                    ft.addToBackStack(Constants.MY_RECIPES)
                    ft.commit()
                }
                Constants.POPULAR -> {
                    val switchTo = PopularFragment.newInstance(viewedBy!!)
                    val ft = activity!!.supportFragmentManager.beginTransaction()
                    ft.replace(R.id.fragment_container, switchTo)
                    activity!!.supportFragmentManager.popBackStackImmediate()
                    ft.commit()
                }
                Constants.FAVORITE -> {
                    val switchTo = FavoriteFragment.newInstance(viewedBy!!)
                    val ft = activity!!.supportFragmentManager.beginTransaction()
                    ft.replace(R.id.fragment_container, switchTo)
                    activity!!.supportFragmentManager.popBackStackImmediate()
                    ft.commit()
                }
                Constants.SEARCH -> {
                    val switchTo = SearchFragment.newInstance(viewedBy!!)
                    val ft = activity!!.supportFragmentManager.beginTransaction()
                    ft.replace(R.id.fragment_container, switchTo)
                    activity!!.supportFragmentManager.popBackStackImmediate()
                    ft.commit()
                }
            }
        }
        if(viewedBy == recipe?.uid) {
            view.button_edit_recipe.setOnClickListener {
                val builder = AlertDialog.Builder(context!!)
                val editTextIds = ArrayList<Int>()
                // Set options
                val view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_recipe, null, false)
                builder.setView(view)
                Picasso.get().load(recipe?.url).into(view.recipe_image)
                builder.setPositiveButton(android.R.string.ok, null)
                builder.setNeutralButton("+", null)
                builder.setNegativeButton(android.R.string.cancel, null)
                view.recipe_image.setOnClickListener {
                    into = view.recipe_image
                    showPictureDialog()
                }
                val titleEditText = view.findViewById<EditText>(R.id.edit_title)
                titleEditText.setText(recipe?.title)
                val dialog = builder.create()
                val layout = view.findViewById<RelativeLayout>(R.id.edit_recipe_layout)
                var lastID = R.id.ingredients_edit_text
                var nextEditText = EditText(context)
                val instructionsText = view.findViewById<EditText>(R.id.instructions_edit_text)
                instructionsText.text.insert(0, recipe?.instructions)
                instructionsText.hint = context!!.resources.getString(R.string.instructions)
                for (ingredient in recipe?.ingredients ?: ArrayList()) {
                    view.findViewById<EditText>(lastID).text.insert(0, ingredient)
                    nextEditText = EditText(context)
                    nextEditText.id = View.generateViewId()
                    nextEditText.hint = context!!.resources.getString(R.string.ingredient)
                    val layoutParams: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    layoutParams.addRule(RelativeLayout.ALIGN_END, lastID)
                    layoutParams.addRule(RelativeLayout.ALIGN_START, lastID)
                    layoutParams.addRule(RelativeLayout.BELOW, lastID)
                    lastID = nextEditText.id
                    nextEditText.layoutParams = layoutParams
                    layout.addView(nextEditText)
                    editTextIds.add(lastID)
                }
                layout.removeView(nextEditText)
                editTextIds.removeAt(editTextIds.size - 1)
                if (editTextIds.size != 0) {
                    lastID = editTextIds[editTextIds.size - 1]
                }

                dialog.setOnShowListener {
                    val neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
                    neutralButton.setOnClickListener {
                        nextEditText = EditText(context)
                        nextEditText.id = View.generateViewId()
                        nextEditText.hint = context!!.resources.getString(R.string.ingredient)
                        val layoutParams: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        layoutParams.addRule(RelativeLayout.ALIGN_END, lastID)
                        layoutParams.addRule(RelativeLayout.ALIGN_START, lastID)
                        layoutParams.addRule(RelativeLayout.BELOW, lastID)
                        lastID = nextEditText.id
                        nextEditText.layoutParams = layoutParams
                        layout.addView(nextEditText)
                        editTextIds.add(lastID)
                    }
                    val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    positiveButton.setOnClickListener { dialogView: View ->
                        val title = titleEditText.text.toString()
                        val instructions = view.instructions_edit_text.text.toString()
                        val ingredientList = ArrayList<String>()
                        for (id in editTextIds) {
                            val ingredient = view.findViewById<EditText>(id).text.toString()
                            ingredientList.add(ingredient)
                        }
                        val r = Recipe(title, ingredientList, instructions, recipe!!.uid)
                        FirebaseFirestore.getInstance().collection(Constants.RECIPES_PATH)
                            .document(recipe!!.id).set(r)
                        it.dismiss()
                    }
                    val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    negativeButton.setOnClickListener { dialogView: View ->
                        it.dismiss()
                    }
                }
                /*
                the following loop was intended to enable users to remove empty editTexts, but
                for this to work, I think the editText would have to be set to not focusable.
                kept in case this or similar code might be useful in the future. Doesn't seem
                to do anything otherwise.
                 */
                /*for((index, id) in editTextIds.withIndex()) {
                    val et = view.findViewById<EditText>(id)
                    et.setOnLongClickListener {
                        if(et.text.toString() != "") {
                            return@setOnLongClickListener true
                        }
                        if(index == editTextIds.size) {
                            layout.removeView(et)
                            return@setOnLongClickListener true
                        }
                        var aboveID = -1
                        if(index == 0) {
                            aboveID = view.findViewById<EditText>(R.id.instructions_edit_text).id
                        }
                        else {
                            aboveID = editTextIds[index-1]
                        }
                        val layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                        layoutParams.addRule(RelativeLayout.BELOW, aboveID)
                        layoutParams.addRule(RelativeLayout.ALIGN_START, aboveID)
                        layoutParams.addRule(RelativeLayout.ALIGN_END, aboveID)
                        view.findViewById<EditText>(editTextIds[id+1]).layoutParams = layoutParams
                        layout.removeView(et)
                        true
                    }
                }*/
                dialog.show()
            }
        }
        else {//then viewedBy != recipe?.uid
            val r = recipe!!.clone()
            val recipes = ArrayList<Recipe>()
            val recipesRef = FirebaseFirestore.getInstance().collection(Constants.USERS_PATH)
            recipesRef.whereEqualTo("favoriteOf", viewedBy).get().addOnSuccessListener {
                for(doc in it.documents) {
                    val recipe = Recipe.fromSnapshot(doc)
                    var unique = true
                    for(r1 in recipes) {
                        if(r1.equals(recipe)) {
                            unique = false
                            recipesRef.document(r1.id).delete()
                        }
                    }
                    if(unique) {
                        recipes.add(recipe)
                    }
                }
                view.button_edit_recipe.setBackgroundResource(R.mipmap.ic_action_favorite)
                for(r2 in recipes) {
                    if (r.equals(r2)) {
                        view.button_edit_recipe.setBackgroundResource(R.mipmap.ic_favorite)
                        break
                    }
                }
                view.button_edit_recipe.text = ""
                view.button_return.height *= 2
                view.button_edit_recipe.height *= 2
                var contains = false
                recipesRef.get().addOnSuccessListener {
                    for(doc in it.documents) {
                        val cur = Recipe.fromSnapshot(doc)
                        if(cur.favoriteOf == viewedBy && cur.equals(r)) {
                            contains = true
                        }
                    }
                }
                view.button_edit_recipe.setOnLongClickListener {
                    if (contains) {
                        contains = false
                        unFavorite(view, r)
                    }
                    else {
                        contains = true
                        favorite(view, r)
                    }
                    true
                }
            }
        }
        return view
    }

    private fun showPictureDialog() {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle("Choose a photo source")
        builder.setMessage("Would you like to take a new picture?\nOr choose an existing one?")
        builder.setPositiveButton("Take Picture") { _, _ ->
            launchCameraIntent()
        }

        builder.setNegativeButton("Choose Picture") { _, _ ->
            launchChooseIntent()
        }
        builder.create().show()
    }

    // Everything camera- and storage-related is from
    // https://developer.android.com/training/camera/photobasics
    private fun launchCameraIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    // authority declared in manifest
                    val photoURI: Uri = FileProvider.getUriForFile(
                        context!!,
                        "edu.rosehulman.catchandkit",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, RC_TAKE_PICTURE)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: url for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun launchChooseIntent() {
        // https://developer.android.com/guide/topics/providers/document-provider
        val choosePictureIntent = Intent(
            Intent.ACTION_OPEN_DOCUMENT,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        choosePictureIntent.addCategory(Intent.CATEGORY_OPENABLE)
        choosePictureIntent.type = "image/*"
        if (choosePictureIntent.resolveActivity(context!!.packageManager) != null) {
            startActivityForResult(choosePictureIntent, RC_CHOOSE_PICTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                RC_TAKE_PICTURE -> {
                    sendCameraPhotoToAdapter()
                }
                RC_CHOOSE_PICTURE -> {
                    sendGalleryPhotoToAdapter(data)
                }
            }
        }
    }

    private fun sendCameraPhotoToAdapter() {
        addPhotoToGallery()
        Log.d(Constants.TAG, "Sending to adapter this photo: $currentPhotoPath")
        //adapter.add(currentPhotoPath)
    }

    private fun addPhotoToGallery() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(currentPhotoPath)
            mediaScanIntent.data = Uri.fromFile(f)
            activity!!.sendBroadcast(mediaScanIntent)
        }
    }

    private fun sendGalleryPhotoToAdapter(data: Intent?) {
        if (data != null && data.data != null) {
            val location = data.data!!.toString()
            ImageRescaleTask(location).execute()
            //adapter.add(location)
        }
    }

    private fun favorite(view: View, r: Recipe) {
        view.button_edit_recipe.setBackgroundResource(R.mipmap.ic_favorite)
        r.favoriteOf = viewedBy!!
        FirebaseFirestore.getInstance().collection(Constants.USERS_PATH).add(r)
    }

    private fun unFavorite(view: View, r: Recipe) {
        view.button_edit_recipe.setBackgroundResource(R.mipmap.ic_action_favorite)
        FirebaseFirestore.getInstance().collection(Constants.USERS_PATH).get().addOnSuccessListener {
            for(doc in it.documents) {
                val cur = Recipe.fromSnapshot(doc)
                if(viewedBy == cur.favoriteOf && r.equals(cur)) {
                    FirebaseFirestore.getInstance().collection(Constants.USERS_PATH).document(doc.id).delete()
                    break
                }
            }
        }
    }

    inner class ImageRescaleTask(val localPath: String) : AsyncTask<Void, Void, Bitmap>() {
        override fun doInBackground(vararg p0: Void?): Bitmap? {
            // Reduces length and width by a factor (currently 2).
            val ratio = 2
            return BitmapUtils.rotateAndScaleByRatio(context!!, localPath, ratio)
        }

        override fun onPostExecute(bitmap: Bitmap?) {
            // TODO: Write and call a new storageAdd() method with the url and bitmap
            // that uses Firebase storage.
            // https://firebase.google.com/docs/storage/android/upload-files
            storageAdd(localPath, bitmap)
        }

        fun storageAdd(myPath: String, bitmap: Bitmap?): String? {
            val baos = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val bytes = baos.toByteArray()
            val id = Math.abs(Random.nextLong()).toString()
            var uploadTask = storageRef.child(id).putBytes(bytes)
            uploadTask.addOnFailureListener {
                // Handle unsuccessful uploads
            }.addOnSuccessListener {
                // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                // ...
                picId = id.toLong()
            }

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation storageRef.child(id).downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    url = downloadUri.toString()
                    Log.d("picasso", "not loading")
                    if(into!=null) {
                        Log.d("picasso", "started loading")
                        Picasso.get().load(url).into(into)
                    }
                } else {
                    // Handle failures
                    // ...
                }
            }.addOnFailureListener {
                Log.d("storage", "failed")
            }
            return null
        }
    }

    companion object {
        fun newInstance(recipe: Recipe, previousFragment: String, viewedBy: String = "") =
            RecipeFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(Constants.ARG_RECIPE, recipe)
                    putString(Constants.ARG_PREVIOUS, previousFragment)
                    if(viewedBy != "") {
                        putString(Constants.VIEWED_BY, viewedBy)
                    }
                }
            }
    }
}
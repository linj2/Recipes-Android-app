package edu.rosehulman.menga.recipetracker

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dialog_edit_recipe.view.*
import kotlinx.android.synthetic.main.recipe_view.*
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
    private lateinit var viewedBy: FirebaseUser
    val storageRef = FirebaseStorage.getInstance().reference.child(Constants.IMAGES_PATH)



    private var currentPhotoPath = ""
    var url = ""
    var into: ImageView? = null
    private var picId: Long = -1
    var willDelete: Long = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {
            recipe = it?.getParcelable(Constants.ARG_RECIPE)
            previous = it?.getString(Constants.ARG_PREVIOUS)
            viewedBy = it?.getParcelable(Constants.ARG_USER)!!
        }
    }

    private fun updateView() {
        val holder = view!!.findViewById<LinearLayout>(R.id.ingredients_holder)
        holder.removeAllViews()
        for(ingredient in recipe!!.ingredients) {
            val textView = TextView(context)
            textView.text = ingredient
            val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
            params.addRule(RelativeLayout.CENTER_HORIZONTAL)
            holder.addView(textView)
        }
        view!!.button_edit_recipe.setOnClickListener {
            //remove old onClickListener
        }
        view!!.button_edit_recipe.setOnLongClickListener {
            Toast.makeText(context, context!!.resources.getString(R.string.edit_warning), Toast.LENGTH_SHORT).show()
            true
        }
        view!!.instructions_view.text = recipe!!.instructions
        Picasso.get().load(recipe!!.url).placeholder(R.mipmap.ic_launcher).into(view!!.recipe_image_view)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.recipe_view, container, false)

        //comment button
        val showCommentB = view!!.findViewById<Button>(R.id.Button_show_comment)
        showCommentB.setOnClickListener {
            val switchTo = CommentsFragment.newInstance(viewedBy,recipe!!.id)
            val ft = activity!!.supportFragmentManager.beginTransaction()
            ft.replace(R.id.fragment_container, switchTo)
            ft.addToBackStack(Constants.COMMENT)
            ft.commit()
        }

        val buttonHolder = view.findViewById<LinearLayout>(R.id.holder_buttons)
        if ((previous == Constants.SEARCH || previous == Constants.POPULAR) && viewedBy.uid != recipe?.uid) {
            buttonHolder.removeView(view.findViewById(R.id.button_delete))
        }
        view.recipe_view_title.text = recipe?.title
        val ingredientsHolder = view.ingredients_holder
        for(ingredient in recipe!!.ingredients) {
            val textView = TextView(context)
            textView.text = ingredient
            val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                     ViewGroup.LayoutParams.WRAP_CONTENT)
            params.addRule(RelativeLayout.CENTER_HORIZONTAL)
            ingredientsHolder.addView(textView)
        }
//        val instructionsParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                                                            ViewGroup.LayoutParams.WRAP_CONTENT)
//        instructionsParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
//        instructionsParams.addRule(RelativeLayout.BELOW, lastID)
//        view.instructions_view.layoutParams = instructionsParams

        view.instructions_view.text = recipe?.instructions
        if(recipe?.picId == (-1).toLong()) {

        }
        else {
            Log.d("mmmmmmmmmmmmmm", "nnnnnnnnnnnnnnnnnn")
            Picasso.get()
                .load(recipe?.url)
                .fit()
                .into(view.recipe_image_view)
        }
        if(previous == Constants.FAVORITE) {
            view.button_delete.text = context!!.resources.getString(R.string.remove)
        }
        else(Log.d(previous, Constants.MY_RECIPES))
        if ((previous != Constants.SEARCH && previous != Constants.POPULAR) || viewedBy.uid == recipe?.uid) {
            view.button_delete.setOnLongClickListener {
                if ((viewedBy.uid != recipe?.uid && previous != Constants.FAVORITE) || viewedBy.uid != recipe?.uid) {
                    Toast.makeText(context, context!!.resources.getString(R.string.remove_warning), Toast.LENGTH_SHORT).show()
                } else if(previous == Constants.FAVORITE) {
                    val builder = AlertDialog.Builder(context!!)
                    builder
                        .setMessage(context!!.resources.getString(R.string.remove_message))
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            FirebaseFirestore.getInstance().collection(Constants.FAVORITE_PATH).document(recipe!!.id).delete()
                            fragmentManager?.popBackStackImmediate()
                            storageRef.child(recipe!!.picId.toString()).delete()
                        }
                        .setNegativeButton(android.R.string.no, null)
                        .create().show()
                }
                else {
                    val builder = AlertDialog.Builder(context!!)
                    builder
                        .setMessage(context!!.resources.getString(R.string.delete_prompt))
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
        if(viewedBy.uid == recipe?.uid) {
            view.button_edit_recipe.setTextColor(resources.getColor(android.R.color.white))
            view.button_edit_recipe.backgroundTintList = resources.getColorStateList(R.color.fui_transparent)

            view.button_edit_recipe.setOnClickListener {
                val builder = AlertDialog.Builder(context!!)
                val editTextIds = ArrayList<Int>()
                // Set options
                val ret = LayoutInflater.from(context).inflate(R.layout.dialog_edit_recipe, null, false)
                builder.setView(ret)
                if(recipe?.url!="") {

                    Picasso.get().load(recipe?.url).fit().into(ret.recipe_image)
                }
                builder.setPositiveButton(android.R.string.ok, null)
                builder.setNeutralButton(context!!.resources.getString(R.string.plus), null)
                builder.setNegativeButton(android.R.string.cancel, null)
                ret.recipe_image.setOnClickListener {
                    into = ret.recipe_image
                    willDelete = recipe!!.picId
                    showPictureDialog()
                }
                val titleEditText = ret.findViewById<EditText>(R.id.edit_title)
                titleEditText.setText(recipe?.title)
                val dialog = builder.create()
                val layout = ret.findViewById<RelativeLayout>(R.id.edit_recipe_layout)
                var lastID = R.id.ingredients_edit_text
                var nextEditText = EditText(context)
                val instructionsText = ret.findViewById<EditText>(R.id.instructions_edit_text)
                instructionsText.text.insert(0, recipe?.instructions)
                instructionsText.hint = context!!.resources.getString(R.string.instructions)
                editTextIds.add(lastID)
                for (ingredient in recipe?.ingredients ?: ArrayList()) {
                    ret.findViewById<EditText>(lastID).text.insert(0, ingredient)
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
                        val instructions = ret.instructions_edit_text.text.toString()
                        val ingredientList = ArrayList<String>()
                        for (id in editTextIds) {
                            val ingredient = ret.findViewById<EditText>(id).text.toString()
                            if(ingredient != "") {
                                ingredientList.add(ingredient)
                            }
                        }
                        if(picId == (-1).toLong()) {
                            picId = recipe!!.picId
                            url = recipe!!.url
                        }
                        val r = Recipe(title, ingredientList, instructions, recipe!!.uid, "", picId, url)
                        url = ""
                        into = null
                        picId = -1
                        willDelete = -1
                        FirebaseFirestore.getInstance().collection(Constants.RECIPES_PATH)
                            .document(recipe!!.id).set(r)
                        it.dismiss()
                        recipe = r
                        updateView()
                    }
                    val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    negativeButton.setOnClickListener { dialogView: View ->
                        url = ""
                        into = null
                        picId = -1
                        willDelete = -1
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
        else {//then viewedBy?.uid != recipe?.uid
            val r = recipe!!.clone()
            val recipes = ArrayList<Recipe>()
            val recipesRef = FirebaseFirestore.getInstance().collection(Constants.FAVORITE_PATH)
            recipesRef.whereEqualTo(Constants.FAVORITE_OF, viewedBy).get().addOnSuccessListener {
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
                if(previous == Constants.POPULAR || previous == Constants.SEARCH) {
                    view.button_edit_recipe.height *= 2
//                    view.button_delete.height *= 2
                }
                if(previous == Constants.FAVORITE) {
                    view.button_edit_recipe.height *= 2
                    view.button_delete.height *= 2
                }
                var contains = false
                recipesRef.get().addOnSuccessListener {
                    for(doc in it.documents) {
                        val cur = Recipe.fromSnapshot(doc)
                        if(cur.favoriteOf == viewedBy.uid && cur.equals(r)) {
                            contains = true
                            view.button_edit_recipe.setBackgroundResource(R.mipmap.ic_favorite)
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
        if(previous == Constants.MY_RECIPES) {
            view.button_edit_recipe.height = 2
            view.button_delete.height *= 2
        }
        return view
    }

    private fun showPictureDialog() {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(context!!.resources.getString(R.string.picture_prompt))
        builder.setMessage(context!!.resources.getString(R.string.show_pic_dialog_msg))

        builder.setPositiveButton(context!!.resources.getString(R.string.choose_picture)) { _, _ ->
            launchChooseIntent()
        }
        builder.setNegativeButton(android.R.string.cancel, null)
        builder.create().show()
    }

    // Everything camera- and storage-related is from
    // https://developer.android.com/training/camera/photobasics


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
            if(willDelete != (-1).toLong()) {
                storageRef.child(willDelete.toString()).delete()
            }
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
        r.favoriteOf = viewedBy.uid
        FirebaseFirestore.getInstance().collection(Constants.FAVORITE_PATH).add(r)
    }

    private fun unFavorite(view: View, r: Recipe) {
        view.button_edit_recipe.setBackgroundResource(R.mipmap.ic_action_favorite)
        FirebaseFirestore.getInstance().collection(Constants.FAVORITE_PATH).get().addOnSuccessListener {
            for(doc in it.documents) {
                val cur = Recipe.fromSnapshot(doc)
                if(viewedBy.uid == cur.favoriteOf && r.equals(cur)) {
                    FirebaseFirestore.getInstance().collection(Constants.FAVORITE_PATH).document(doc.id).delete()
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
            // that uses Firebase storage.
            // https://firebase.google.com/docs/storage/android/upload-files
            storageAdd(localPath, bitmap)
        }

        private fun storageAdd(myPath: String, bitmap: Bitmap?): String? {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val bytes = byteArrayOutputStream.toByteArray()
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
                    if(into!=null) {
                        Picasso.get().load(url).fit().into(into)
                    }
                } else {
                    // Handle failures
                    // ...
                }
            }
            return null
        }
    }

    companion object {
        fun newInstance(recipe: Recipe, previousFragment: String, viewedBy: FirebaseUser) =
            RecipeFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(Constants.ARG_RECIPE, recipe)
                    putString(Constants.ARG_PREVIOUS, previousFragment)
                    putParcelable(Constants.ARG_USER, viewedBy)
                }
            }
    }
}
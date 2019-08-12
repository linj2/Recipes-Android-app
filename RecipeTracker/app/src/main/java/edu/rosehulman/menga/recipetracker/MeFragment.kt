package edu.rosehulman.menga.recipetracker

import android.app.Activity
import android.content.Context
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
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_edit_recipe.view.*
import kotlinx.android.synthetic.main.fragment_me.view.*
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

class MeFragment: Fragment() {

    lateinit var user: FirebaseUser
    lateinit var adapter: RecipeAdapter
    private var listener: RecipeAdapter.OnRecipeSelectedListener? = null
    private var currentPhotoPath = ""

    var url = ""
    var into: ImageView? = null
    private var picId: Long = -1

    private val storageRef =
        FirebaseStorage
            .getInstance()
            .reference.child(Constants.IMAGES_PATH)

    companion object {
        fun newInstance(user: FirebaseUser) =
            MeFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(Constants.ARG_USER, user)
                }
            }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context is RecipeAdapter.OnRecipeSelectedListener) {
            listener = context
        }
        else {
            Log.e(Constants.TAG, "Should implement OnRecipeSelectedListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {
            user = it?.getParcelable(Constants.ARG_USER)!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_me,container,false)
//        view.button_return.setOnClickListener {
//            val switchTo = HomeFragment.newInstance(uid!!)
//            val ft = activity!!.supportFragmentManager.beginTransaction()
//            ft.replace(R.id.fragment_container, switchTo)
//            for (i in 0 until activity!!.supportFragmentManager.backStackEntryCount) {
//                activity!!.supportFragmentManager.popBackStackImmediate()
//            }
//            ft.commit()
//        }
        adapter = RecipeAdapter(context!!, listener!!,  user)
        view.recycler_view.adapter = adapter
        view.recycler_view.layoutManager = LinearLayoutManager(context)
        view.recycler_view.setHasFixedSize(true)
        adapter.update()

        view.button_add_recipe.setOnClickListener {
            val builder = AlertDialog.Builder(context!!)
            val editTextIds = ArrayList<Int>()
            // Set options
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_recipe, null, false)
            builder.setView(view)
            builder.setPositiveButton(android.R.string.ok, null)
            builder.setNeutralButton("+", null)
            builder.setNegativeButton(android.R.string.cancel, null)
            view.recipe_image.setOnClickListener {
                into = view.recipe_image
                showPictureDialog()
            }
            val titleEditText = view.findViewById<EditText>(R.id.edit_title)
            val dialog = builder.create()
            var lastID = R.id.ingredients_edit_text
            editTextIds.add(lastID)
            dialog.setOnShowListener {
                val neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
                val layout = view.findViewById<RelativeLayout>(R.id.edit_recipe_layout)
                neutralButton.setOnClickListener {
                    val nextEditText = EditText(context)
                    nextEditText.id = View.generateViewId()
                    nextEditText.hint = context!!.resources.getString(R.string.ingredient)
                    val layoutParams: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    layoutParams.addRule(RelativeLayout.ALIGN_END, lastID)
                    layoutParams.addRule(RelativeLayout.ALIGN_START, lastID)
                    layoutParams.addRule(RelativeLayout.BELOW, lastID)
                    lastID = nextEditText.id
                    nextEditText.layoutParams = layoutParams
                    layout.addView(nextEditText)
                    editTextIds.add(lastID)
                }
                val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                positiveButton.setOnClickListener {dialogView: View ->
                    val title = titleEditText.text.toString()
                    val instructions = view.instructions_edit_text.text.toString()
                    val ingredientList = ArrayList<String>()
                    for(id in editTextIds) {
                        val ingredient = view.findViewById<EditText>(id).text.toString()
                        ingredientList.add(ingredient)
                    }
                    val recipe = Recipe(title, ingredientList, instructions, user.uid, "", picId, url)
                    url = ""
                    adapter.add(recipe)
                    into = null
                    picId = -1
                    url = ""
                    //adapter.update() //issue with listener not updating immediately and instead waiting for button click
                    it.dismiss()
                }
                val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                negativeButton.setOnClickListener {dialogView: View ->
                    into = null
                    picId = -1
                    url = ""
                    it.dismiss()
                }
            }
            dialog.show()
        }
//        listener?.setNavigation(R.id.nav_me)
        return view
    }

    private fun showPictureDialog() {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(context!!.resources.getString(R.string.picture_prompt))
        builder.setMessage(context!!.resources.getString(R.string.show_pic_dialog_msg))

        builder.setPositiveButton(context!!.resources.getString(R.string.choose_picture)) { _, _ ->
            launchChooseIntent()
        }
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
}
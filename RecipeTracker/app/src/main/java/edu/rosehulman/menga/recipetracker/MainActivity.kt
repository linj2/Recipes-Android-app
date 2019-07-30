package edu.rosehulman.menga.recipetracker

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),
//    BottomNavigationView.OnNavigationItemSelectedListener,
    SplashFragment.OnLoginButtonPressedListener, RecipeAdapter.OnRecipeSelectedListener
{
    val collection = FirebaseFirestore.getInstance().collection("collection")

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    var uid :String = ""
    lateinit var authStateListener: FirebaseAuth.AuthStateListener
    // Request code for launching the sign in Intent.
    private val RC_SIGN_IN = 1

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeListeners()

        FirebaseApp.initializeApp(this)

        //add selected listener for bottom navigation view
//        val navView: BottomNavigationView = findViewById(R.id.nav_view)
//        navView.setOnNavigationItemSelectedListener(this)

        //set main fragment as default page
        if (savedInstanceState == null) {
            val fragment = HomeFragment()
            val ft = supportFragmentManager.beginTransaction()
            ft.add(R.id.fragment_container, fragment)
            ft.commit()
        }
        setSupportActionBar(toolbar)
    }

    override fun showRecipe(recipe: Recipe, previous: String, viewedBy: String) {
        val fragment = RecipeFragment.newInstance(recipe, previous, viewedBy)
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, fragment).addToBackStack("recipe").commit()
    }




    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK) {

        }
        return super.onKeyDown(keyCode, event)
    }

    //rest of it just go log in page
    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authStateListener)
    }

    private fun initializeListeners() {
        authStateListener = FirebaseAuth.AuthStateListener { auth: FirebaseAuth ->
            val user = auth.currentUser
            Log.d(Constants.TAG, "In auth listener, user = $user")
            if (user != null) {
                Log.d(Constants.TAG, "UID: ${user.uid}")
                Log.d(Constants.TAG, "Name: ${user.displayName}")
                Log.d(Constants.TAG, "Email: ${user.email}")
                Log.d(Constants.TAG, "Phone: ${user.phoneNumber}")
                Log.d(Constants.TAG, "Photo URL: ${user.photoUrl}")
                uid = user.uid
                switchToHomeFragment(user.uid)
            } else {
                switchToSplashFragment()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //return super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                // User chose the "Settings" item, show the app settings UI...
                if(uid == "") {
                    Toast.makeText(this, "Already logged out", Toast.LENGTH_SHORT).show()
                }
                else auth.signOut()
                true
            }

            else -> {
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun switchToSplashFragment() {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, SplashFragment())
        ft.commit()
    }

    //TODO: might need recipe for myRecipe fragment
    private fun switchToHomeFragment(uid: String) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, HomeFragment.newInstance(uid))
        ft.commitAllowingStateLoss()
    }

    override fun onLoginButtonPressed() {
        launchLoginUI()
    }

    private fun launchLoginUI() {
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        val loginIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.mipmap.ic_launcher_custom)
            .build()

        // Create and launch sign-in intent
        startActivityForResult(loginIntent, RC_SIGN_IN)
    }
}


package edu.rosehulman.menga.recipetracker

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_me.*

class MainActivity : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener,
    SplashFragment.OnLoginButtonPressedListener
{
    val collection = FirebaseFirestore.getInstance().collection("collection")

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    var uid :String = ""
    lateinit var authStateListener: FirebaseAuth.AuthStateListener
    // Request code for launching the sign in Intent.
    private val RC_SIGN_IN = 1

    fun testFirestore(): String {
        var str = "didn't work"
        FirebaseFirestore.getInstance().collection("collection")
            .addSnapshotListener { snapshot, firebaseFirestoreException ->
                if(firebaseFirestoreException != null) {
                    return@addSnapshotListener
                }
                for(docChange in snapshot!!.documents) {
                    Toast.makeText(this, docChange.getString("str"), Toast.LENGTH_SHORT).show()
                }
            }
        return str
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeListeners()

        FirebaseApp.initializeApp(this)
        val str = testFirestore()

        //add selected listener for bottom navigation view
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(this)

        //set main fragment as default page
        if (savedInstanceState == null) {
            val fragment = HomeFragment()
            val ft = supportFragmentManager.beginTransaction()
            ft.add(R.id.fragment_container, fragment)
            ft.commit()
        }
    }

    /*
    TODO: I think the buttons on the home screen will work best for the navigation
    because there are too many items that need to be immediately available to put
    in a bottom nav. I think it would get a bit crowded, go for it. I think it would
    eliminate the need for a homescreen, though
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var switchTo: Fragment? = null
        var backStackString: String? = null
        when (item.itemId) {
            R.id.nav_home-> {
                switchTo = HomeFragment()
            }
            R.id.nav_favorite -> {
                switchTo = FavoriteFragment()
                backStackString = "favorite"
            }
            R.id.nav_me ->{
                switchTo = MeFragment()
                backStackString = "me"
            }
        }
        if (switchTo != null) {
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.fragment_container, switchTo)
            for (i in 0 until supportFragmentManager.backStackEntryCount) {
                supportFragmentManager.popBackStackImmediate()
            }
            if(backStackString!=null) ft.addToBackStack(backStackString)
            ft.commit()
        }
        return true
    }

    //TODO: This is just here because I think it might help with the backstack later
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
                switchToHomeFragment()
            } else {
                switchToSplashFragment()
            }
        }
    }

    private fun switchToSplashFragment() {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, SplashFragment())
        ft.commit()
    }

    //TODO: might need uid for myRecipe fragment
    private fun switchToHomeFragment() {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, HomeFragment())
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


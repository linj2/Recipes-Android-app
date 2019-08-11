package edu.rosehulman.menga.recipetracker

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.util.AttributeSet
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

const val PREFS = "preferences"
const val PREVIOUS = "previous"

class MainActivity : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener,
    SplashFragment.OnLoginButtonPressedListener,
    RecipeAdapter.OnRecipeSelectedListener
{

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    var user: FirebaseUser? = null
    var uid: String = ""
    lateinit var authStateListener: FirebaseAuth.AuthStateListener
    lateinit var navView: BottomNavigationView
    // Request code for launching the sign in Intent.
    private val RC_SIGN_IN = 1
    private lateinit var fragment: Fragment
    private var currentFragment = Constants.POPULAR

    var first = true

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                                                 ViewGroup.LayoutParams.WRAP_CONTENT)
//        params.addRule(RelativeLayout.BELOW, toolbar.id)
//        fragment_container.layoutParams = params
        setSupportActionBar(toolbar)
        user = auth.currentUser!!
        initializeListeners()

        FirebaseApp.initializeApp(this)

        //add selected listener for bottom navigation view
        navView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(this)

        //set main fragment as default page
//        if (savedInstanceState == null) {
//            fragment = PopularFragment.newInstance(user!!)
//            when (currentFragment) {
//                Constants.SEARCH -> fragment = SearchFragment.newInstance(user!!)
//                Constants.MY_RECIPES -> fragment = MeFragment.newInstance(user!!)
//                Constants.FAVORITE -> fragment = FavoriteFragment.newInstance(user!!)
//            }
//            val ft = supportFragmentManager.beginTransaction()
//            ft.add(R.id.fragment_container, fragment)
//            ft.commit()
//        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val switchTo: Fragment
        when (item.itemId) {
            R.id.nav_favorite -> {
                currentFragment = Constants.FAVORITE
                switchTo = FavoriteFragment.newInstance(user!!)
                fragment = switchTo
                val ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.fragment_container, switchTo)
                for (i in 0 until supportFragmentManager.backStackEntryCount) {
                    supportFragmentManager.popBackStackImmediate()
                }
                ft.addToBackStack(Constants.FAVORITE)
                ft.commit()
            }
            R.id.nav_popular ->{
                currentFragment = Constants.POPULAR
                switchTo = PopularFragment.newInstance(user!!)
                fragment = switchTo
                val ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.fragment_container, switchTo)
                for (i in 0 until supportFragmentManager.backStackEntryCount) {
                    supportFragmentManager.popBackStackImmediate()
                }
                ft.addToBackStack(Constants.POPULAR)
                ft.commit()
            }
            R.id.nav_me ->{
                currentFragment = Constants.MY_RECIPES
                switchTo = MeFragment.newInstance(user!!)
                fragment = switchTo
                val ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.fragment_container, switchTo)
                for (i in 0 until supportFragmentManager.backStackEntryCount) {
                    supportFragmentManager.popBackStackImmediate()
                }
                ft.addToBackStack(Constants.MY_RECIPES)
                ft.commit()
            }
            R.id.nav_search ->{
                currentFragment = Constants.SEARCH
                switchTo = SearchFragment.newInstance(user!!)
                fragment = switchTo
                val ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.fragment_container, switchTo)
                for (i in 0 until supportFragmentManager.backStackEntryCount) {
                    supportFragmentManager.popBackStackImmediate()
                }
                ft.addToBackStack(Constants.SEARCH)
                ft.commit()
            }
        }
        return true
    }

    override fun onResume() {
        navView.menu.getItem(0).isChecked = true
        super.onResume()
    }

    override fun showRecipe(recipe: Recipe, previous: String, viewedBy: FirebaseUser) {
        val fragment = RecipeFragment.newInstance(recipe, previous, viewedBy)
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, fragment).addToBackStack(Constants.RECIPE).commit()
    }


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
            user = auth.currentUser

            Log.d(Constants.TAG, "In auth listener, user = $user")
            if (user != null) {
                uid = user!!.uid
                Log.d(Constants.TAG, "UID: ${user!!.uid}")
                Log.d(Constants.TAG, "Name: ${user!!.displayName}")
                Log.d(Constants.TAG, "Email: ${user!!.email}")
                Log.d(Constants.TAG, "Phone: ${user!!.phoneNumber}")
                Log.d(Constants.TAG, "Photo URL: ${user!!.photoUrl}")
                switchToPopularFragment()
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
                if (user == null) {
                    Toast.makeText(this, getString(R.string.already_logged_out), Toast.LENGTH_SHORT).show()
                } else auth.signOut()
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

    private fun switchToPopularFragment() {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, PopularFragment.newInstance(user!!))
        ft.commit()
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
            .setLogo(R.mipmap.ic_launcher)
            .build()

        // Create and launch sign-in intent
        startActivityForResult(loginIntent, RC_SIGN_IN)
    }

    override fun setNavigation(id: Int) {
        navView.selectedItemId = id
    }
}


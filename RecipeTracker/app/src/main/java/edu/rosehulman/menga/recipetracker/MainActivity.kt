package edu.rosehulman.menga.recipetracker

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.view.KeyEvent
import android.view.MenuItem
import kotlinx.android.synthetic.main.fragment_me.*

class MainActivity : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
}


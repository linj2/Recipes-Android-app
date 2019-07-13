package edu.rosehulman.menga.recipetracker

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.view.MenuItem

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
            val fragment = MainFragment()
            val ft = supportFragmentManager.beginTransaction()
            ft.add(R.id.fragment_container, fragment)
            ft.commit()
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var switchTo: Fragment? = null
        when (item.itemId) {
            R.id.nav_home-> {
                switchTo = MainFragment()
            }
            R.id.nav_favorite -> {
                switchTo = FavoriteFragment()
            }
            R.id.nav_me ->{
                switchTo = MeFragment()
            }
        }
        if (switchTo != null) {
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.fragment_container, switchTo)
            for (i in 0 until supportFragmentManager.backStackEntryCount) {
                supportFragmentManager.popBackStackImmediate()
            }
            ft.commit()
        }
        return true
    }

}


package com.zharkovsky.memes.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zharkovsky.memes.R


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.itemIconTintList = null;

        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)

//        val mToolbar = findViewById<Toolbar>(R.id.toolbar)
//        setSupportActionBar(mToolbar);
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        val mSearch: MenuItem = menu.findItem(R.id.action_search)
        val mSearchView: SearchView = mSearch.getActionView() as SearchView
        mSearchView.setQueryHint("Search")
        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                println(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                println(newText)
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }
}
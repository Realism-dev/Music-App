package dev.realism.musicapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dev.realism.data.model.Track
import dev.realism.tracks.ARG_TRACK
import dev.realism.tracks.ARG_TRACK_LIST
import dev.realism.tracks.di.NavigationProvider

class MainActivity : AppCompatActivity(), NavigationProvider {
    private lateinit var navController: NavController
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setNavController()
    }

    override fun navigateToPlayFragment(trackList:List<Track>,track: Track) {
        val bundle = Bundle()
        bundle.putParcelable(ARG_TRACK,track)
        bundle.putParcelableArray(ARG_TRACK_LIST,trackList.toTypedArray())

        // Проверяем, что можно безопасно выполнить навигацию
        navController.currentDestination?.let {
            if (it.id != R.id.fragment_play) {  // проверяем, не находимся ли мы уже на экране PlayFragment
                navController.navigate(R.id.fragment_play, bundle)
            }
        }
    }


    private fun setNavController(){
        // Инициализация NavController
        navController = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController

        // Настройка BottomNavigationView с NavController
        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation.setupWithNavController(navController)

        bottomNavigation.setOnItemSelectedListener { item ->
//            Log.d("BottomNavigation", "Item clicked: ${item.itemId}")
            when (item.itemId) {
                R.id.fragment_api_tracks -> {
                    navController.navigate(R.id.fragment_api_tracks)
                    true
                }
                R.id.fragment_downloaded_tracks -> {
                    navController.navigate(R.id.fragment_downloaded_tracks)
                    true
                }
                else -> false
            }
        }
    }
}



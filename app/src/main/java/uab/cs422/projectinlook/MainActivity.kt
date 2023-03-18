package uab.cs422.projectinlook

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.google.android.material.navigation.NavigationView
import uab.cs422.projectinlook.databinding.ActivityMainBinding
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.mainToolbar)

        val navView = binding.navDrawerView
        val drawerLayout = binding.mainDrawerLayout

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_day, R.id.navigation_week, R.id.navigation_month
            )
        )

        binding.mainToolbar.setNavigationOnClickListener {
            drawerLayout.open()
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_item_settings -> {
                    drawerLayout.close()
                    startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                    true
                }
                R.id.navigation_item_day -> navController.navigate(R.id.navigation_day)
                R.id.navigation_item_week -> navController.navigate(R.id.navigation_week)
                R.id.navigation_item_month -> navController.navigate(R.id.navigation_month)
            }
            uncheckAll(navView.menu)
            menuItem.isChecked = true
            drawerLayout.close()
            true
        }
        checkCurrentDestination()

        supportActionBar?.title =
            LocalDateTime.now().month.getDisplayName(TextStyle.FULL, Locale.US)
    }

    /**
     * Unchecks all menu items for given menu (only depth of 2)
     *
     * @param menu Menu for unchecking
     */
    private fun uncheckAll(menu: Menu) {
        val size = menu.size()
        for (i in 0 until size) {
            val item = menu.getItem(i)
            if (item.hasSubMenu()) {
                for (j in 0 until (item.subMenu?.size() ?: 0)) {
                    if (item.subMenu?.getItem(j)?.isCheckable == true) {
                        item.subMenu?.getItem(j)?.isChecked = false
                    }
                }
            }
        }
    }

    /**
     * Checks the current destination that the MainActivity is showing in the NavigationDrawer
     */
    private fun checkCurrentDestination() {
        val navView: NavigationView = binding.navDrawerView
        val currentDest = findNavController(R.id.nav_host_fragment_activity_main).currentDestination
        navView.menu.forEach { menuItem ->
            menuItem.subMenu?.forEach {
                println("${it.title}, ${currentDest?.label}")
                it.isChecked = it.title == currentDest?.label
            }

        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        checkCurrentDestination()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onRestart() {
        super.onRestart()
        recreate()
    }
}
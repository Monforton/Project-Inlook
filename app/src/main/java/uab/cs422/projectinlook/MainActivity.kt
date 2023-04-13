package uab.cs422.projectinlook

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColor
import androidx.core.view.forEach
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.preference.PreferenceManager
import com.google.android.material.navigation.NavigationView
import uab.cs422.projectinlook.databinding.ActivityMainBinding
import uab.cs422.projectinlook.entities.CalEvent
import uab.cs422.projectinlook.ui.CalendarInterface
import uab.cs422.projectinlook.util.runOnIO
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dao: EventDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onNavigateUp()
                checkCurrentDestination()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)

        dao = EventDatabase.getInstance(this).eventDao

        // Set up Toolbar as ActionBar
        setSupportActionBar(binding.mainToolbar)

        val navView = binding.navDrawerView
        val drawerLayout = binding.mainDrawerLayout
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val navGraph = navController.navInflater.inflate(R.navigation.mobile_navigation)

        when (PreferenceManager.getDefaultSharedPreferences(this)
            .getString("start_destination", "")) {
            "today" -> navGraph.setStartDestination(R.id.navigation_today)
            "day" -> navGraph.setStartDestination(R.id.navigation_day)
            "week" -> navGraph.setStartDestination(R.id.navigation_week)
            "month" -> navGraph.setStartDestination(R.id.navigation_month)
            else -> navGraph.setStartDestination(R.id.navigation_today)
        }
        navController.graph = navGraph

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
                R.id.navigation_item_today -> navController.navigate(R.id.navigation_today)
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

        binding.btnToday.setOnClickListener {
            ((supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment)
                .childFragmentManager.fragments[0] as CalendarInterface).onTodayButtonClicked()
        }

        binding.fabAdd.setOnClickListener {
            val typedValue = TypedValue()
            theme.resolveAttribute(
                com.google.android.material.R.attr.colorPrimaryContainer,
                typedValue,
                true
            )
            runOnIO {
                dao.insertEvent(
                    CalEvent(
                        startTime = LocalDateTime.of(
                            2023,
                            LocalDateTime.now().month,
                            LocalDateTime.now().dayOfMonth,
                            12,
                            0,
                        ),
                        endTime = LocalDateTime.of(
                            2023,
                            LocalDateTime.now().month,
                            LocalDateTime.now().dayOfMonth,
                            14,
                            0,
                        ),
                        title = "event 1",
                        color = typedValue.data.toColor()
                    )
                )
            }
            ((supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment)
                .childFragmentManager.fragments[0] as CalendarInterface).updateEvents()
        }
    }

    override fun onRestart() {
        super.onRestart()
        checkCurrentDestination()
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
}
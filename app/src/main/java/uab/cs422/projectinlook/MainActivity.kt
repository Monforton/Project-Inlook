package uab.cs422.projectinlook

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColor
import androidx.core.view.forEach
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.navigation.NavigationView
import uab.cs422.projectinlook.databinding.ActivityMainBinding
import uab.cs422.projectinlook.entities.CalEvent
import uab.cs422.projectinlook.ui.CalendarInterface
import uab.cs422.projectinlook.util.runOnIO
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var dao: EventDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dao = EventDatabase.getInstance(this).eventDao


        // Set up Toolbar as ActionBar
        setSupportActionBar(binding.mainToolbar)

        val navView = binding.navDrawerView
        val drawerLayout = binding.mainDrawerLayout

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

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

        binding.fabAdd.setOnClickListener {



            val typedValue = TypedValue()
            theme.resolveAttribute(
                com.google.android.material.R.attr.colorPrimaryContainer,
                typedValue,
                true

            )
            runOnIO {
                AlertDialog.Builder(it.context)
                    .setTitle("New Event")
                    .setMessage("")
                    .setPositiveButton("Edit") { dialogInterface: DialogInterface, i: Int ->
                        val editableTitle = EditText(it.context)
                        val editableBody = EditText(it.context)
                    }

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
            ((supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment).childFragmentManager.fragments[0] as CalendarInterface).updateEvents()
        }
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


    // TODO Find non-deprecated way of doing this?
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        checkCurrentDestination()
    }


    override fun onRestart() {
        super.onRestart()
        recreate()
    }
}
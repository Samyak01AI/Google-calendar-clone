package com.example.googlecalendarapplication

import android.Manifest
import android.R.id.toggle
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.googlecalendarapplication.databinding.ActivityMainBinding
import com.example.googlecalendarapplication.repository.EventRepository
import java.time.LocalDate

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private val eventViewModel: EventViewModel by viewModels {
        EventViewModelFactory(
            repository = EventRepository(AppDatabase.getDatabase(this).eventDao()),
            application = application
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        setupDrawer()
        setupViewPager()
        setupFab()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        binding.fabAdd.setOnClickListener {
            val today = LocalDate.now()

            val bottomSheet = AddEventBottomSheet(today) { event ->
                eventViewModel.insert(event)
            }
            bottomSheet.show(supportFragmentManager, "AddEventTag")
        }
    }
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupDrawer() {
        // 1. Connect the DrawerLayout and the Toolbar
        toggle = ActionBarDrawerToggle(
            this,
            binding.main,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.navigationView.itemIconTintList = null
        binding.main.addDrawerListener(toggle)
        toggle.syncState()

        // 2. Handle Menu Clicks
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_month -> {
                    // Already on Month view, just close drawer
                }
                R.id.nav_day, R.id.nav_schedule -> {
                    Toast.makeText(this, "View not implemented yet", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_settings -> {
                    Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
                }
            }
            binding.main.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            // Open the Add Event Bottom Sheet
            val bottomSheet = AddEventBottomSheet(LocalDate.now()) { event ->
                // eventViewModel.insert(event) -> Make sure you have the ViewModel here
            }
            bottomSheet.show(supportFragmentManager, "AddEvent")
        }
    }

    // Handle the "Back" button to close drawer first if it's open
    override fun onBackPressed() {
        if (binding.main.isDrawerOpen(GravityCompat.START)) {
            binding.main.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
    private fun setupViewPager() {
        val viewPager = binding.calendarViewPager

        // Set the adapter to generate fragments
        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = Int.MAX_VALUE // "Infinite" scrolling

            override fun createFragment(position: Int): Fragment {
                val diff = position - (Int.MAX_VALUE / 2)
                val monthDate = LocalDate.now().plusMonths(diff.toLong())

                return MonthFragment.newInstance(monthDate)
            }
        }

        viewPager.setCurrentItem(Int.MAX_VALUE / 2, false)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val diff = position - (Int.MAX_VALUE / 2)
                val currentMonth = LocalDate.now().plusMonths(diff.toLong())

                binding.tvCurrentMonth.text = "${currentMonth.month} ${currentMonth.year}"
            }
        })
    }
}
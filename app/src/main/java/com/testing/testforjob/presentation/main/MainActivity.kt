package com.testing.testforjob.presentation.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.testing.testforjob.databinding.ActivityMainBinding
import com.testing.testforjob.presentation.contacts.ContactsFragment
import com.testing.testforjob.presentation.info.InfoFragment
import com.testing.testforjob.presentation.posts.PostsFragment
import com.testing.testforjob.utils.ViewPagerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        setupViewPager(binding.viewPager)
        listeners()

        /*
        * The use of the nevigation component is avoided due to
        * a known bug that makes it difficult to save the states
        * of the fragments associated with the bottomnavigation
        * */
    }

    private fun listeners() {
        binding.bottomBar.setOnItemSelectedListener {
            when (it) {
                0 -> {
                    binding.toolbarTitle.text = "Posts"
                    binding.viewPager.currentItem = 0
                }
                1 -> {
                    binding.toolbarTitle.text = "Contacts"
                    binding.viewPager.currentItem = 1
                }
                2 -> {
                    binding.toolbarTitle.text = "My Info"
                    binding.viewPager.currentItem = 2
                }
            }
        }

        binding.bottomBar.setOnItemReselectedListener {
            when (it) {
                0 -> {
                    binding.viewPager.currentItem = 0
                }
                1 -> {
                    binding.viewPager.currentItem = 1
                }
                2 -> {
                    binding.viewPager.currentItem = 2
                }
            }
        }


        binding.viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                binding.bottomBar.itemActiveIndex = position
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        binding.toolbarTitle.text = "Posts"
                    }
                    1 -> {
                        binding.toolbarTitle.text = "Contacts"
                    }
                    2 -> {
                        binding.toolbarTitle.text = "My Info"
                    }
                }
                binding.bottomBar.itemActiveIndex = position
                super.onPageSelected(position)
            }
        })
    }

    private fun setupViewPager(viewPager: ViewPager2) {
        val myAdapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        myAdapter.addFragment(PostsFragment())
        myAdapter.addFragment(ContactsFragment())
        myAdapter.addFragment(InfoFragment())
        viewPager.offscreenPageLimit = 3
        viewPager.adapter = myAdapter
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
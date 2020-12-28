package com.example.e_commerce.ui.main

import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.e_commerce.R
import com.example.e_commerce.databinding.ActivityMainBinding
import com.example.e_commerce.ui.base.BaseActivity
import com.example.e_commerce.ui.base.BaseFragment
import com.example.e_commerce.utils.OnNavigationUpdateListner
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(), OnNavigationUpdateListner {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupNavigation()
    }

    /**
     * Initialize Navigation Graph
     * */
    private fun setupNavigation() {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(getViewDataBinding().navigationBottom, navController)
        BaseFragment.listner = this
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onNavigationUpdated(show: Boolean) {
        getViewDataBinding().showNavigation = show
    }
}
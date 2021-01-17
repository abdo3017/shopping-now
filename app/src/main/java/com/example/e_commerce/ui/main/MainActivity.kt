package com.example.e_commerce.ui.main

import android.os.Bundle
import android.view.View
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.e_commerce.R
import com.example.e_commerce.databinding.ActivityMainBinding
import com.example.e_commerce.databinding.DrawerHeaderBinding
import com.example.e_commerce.ui.base.BaseActivity
import com.example.e_commerce.ui.base.BaseFragment
import com.example.e_commerce.utils.OnNavigationUpdateListner
import com.example.e_commerce.utils.PrefManager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(), OnNavigationUpdateListner,
    DrawerLayout.DrawerListener {
    private lateinit var drawerHeaderBinding: DrawerHeaderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        val radius = resources.getDimension(R.dimen._16sdp)
        val navigationView: NavigationView = getViewDataBinding().navView
        val navViewBackground = navigationView.background as MaterialShapeDrawable
        navViewBackground.shapeAppearanceModel = navViewBackground.shapeAppearanceModel
            .toBuilder()
            .setTopRightCorner(CornerFamily.ROUNDED, radius)
            .setBottomRightCorner(CornerFamily.ROUNDED, radius)
            .build()
        getViewDataBinding().drawerLayout.addDrawerListener(this)
        drawerHeaderBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.drawer_header,
            getViewDataBinding().navView,
            false
        )
        getViewDataBinding().navView.addView(drawerHeaderBinding.root)

        drawerHeaderBinding.btnOrders.setOnClickListener {
            getViewDataBinding().drawerLayout.closeDrawer(GravityCompat.START)
            findNavController(R.id.nav_host_fragment).navigate(R.id.myOrderFragment)
        }
        drawerHeaderBinding.btnLogout.setOnClickListener {
            getViewDataBinding().drawerLayout.closeDrawer(GravityCompat.START)
            PrefManager.deleteRememberMe()
            PrefManager.deleteCustomer()
            findNavController(R.id.nav_host_fragment).navigate(R.id.loginFragment)
        }

        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(getViewDataBinding().navigationBottom, navController)
        BaseFragment.listner = this
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onNavigationUpdated(show: Boolean) {
        getViewDataBinding().showNavigation = show
        if (show) {
            drawerHeaderBinding.user = PrefManager.getCustomer()
            getViewDataBinding().drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        } else {
            getViewDataBinding().drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
    }

    override fun onDrawerOpened(drawerView: View) {
    }

    override fun onDrawerClosed(drawerView: View) {
    }

    override fun onDrawerStateChanged(newState: Int) {
    }
}
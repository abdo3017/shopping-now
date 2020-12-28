package com.example.e_commerce.ui.main

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentSplashBinding
import com.example.e_commerce.ui.base.BaseFragment
import com.example.e_commerce.utils.PrefManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SplashFragment :
    BaseFragment<FragmentSplashBinding, Any>(false) {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        setViews()
        return getMRootView()
    }


    override fun getViewModel() = Any()


    private fun setViews() {
        getViewDataBinding().lifecycleOwner = this
        Handler().postDelayed({
            if (PrefManager.getRememberMe() != null) {
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToHomeFragment())
            } else {
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
            }
        }, 1000)
    }

    override val layoutId: Int
        get() = R.layout.fragment_splash
    override val bindingVariableId: Int
        get() = 0
    override val bindingVariableValue: Any
        get() = getViewModel()

}
package com.example.e_commerce.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseActivity<viewDataBinding : ViewDataBinding> : AppCompatActivity() {

    private lateinit var mViewDataBinding: viewDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        performDataBinding()
    }

    private fun performDataBinding() {
        // Binding Class
        mViewDataBinding = DataBindingUtil.setContentView(this, getLayoutId())
        mViewDataBinding.lifecycleOwner = this
    }

    abstract fun getLayoutId(): Int


    fun getViewDataBinding(): viewDataBinding {
        return mViewDataBinding
    }

}
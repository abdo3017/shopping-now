package com.example.e_commerce.utils

import android.content.Context
import android.preference.PreferenceManager
import com.example.e_commerce.datasource.models.Customers
import com.example.e_commerce.ui.main.MyApplication
import com.google.gson.Gson

object PrefManager {
    fun saveCustomer(profile: Customers?, context: Context? = MyApplication.applicationContext()) {
        val editor =
            PreferenceManager.getDefaultSharedPreferences(context).edit()
        val gson = Gson()
        val json = gson.toJson(profile)
        editor.putString(Constants.CUSTOMER, json)
        editor.apply()
    }

    fun getCustomer(context: Context? = MyApplication.applicationContext()): Customers? {
        val preferences =
            PreferenceManager.getDefaultSharedPreferences(context)
        val gson = Gson()
        val json = preferences.getString(Constants.CUSTOMER, null)
        return gson.fromJson(json, Customers::class.java)
    }


    fun deleteCustomer(context: Context? = MyApplication.applicationContext()) {
        val editor =
            PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.remove(Constants.CUSTOMER)
        editor.apply()
    }

    fun saveRememberMe(remember: String?, context: Context? = MyApplication.applicationContext()) {
        val editor =
            PreferenceManager.getDefaultSharedPreferences(context).edit()
        val gson = Gson()
        val json = gson.toJson(remember)
        editor.putString(Constants.REMEMBER_ME, json)
        editor.apply()
    }

    fun getRememberMe(context: Context? = MyApplication.applicationContext()): String? {
        val preferences =
            PreferenceManager.getDefaultSharedPreferences(context)
        val gson = Gson()
        val json = preferences.getString(Constants.REMEMBER_ME, null)
        return gson.fromJson(json, String::class.java)
    }


    fun deleteRememberMe(context: Context? = MyApplication.applicationContext()) {
        val editor =
            PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.remove(Constants.REMEMBER_ME)
        editor.apply()
    }

}
package id.sireto.reviewjujuradministrator.utils

import android.content.SharedPreferences
import id.sireto.reviewjujur.utils.Constants

object SharedPref {
    lateinit var sharedPreferences : SharedPreferences

    fun saveToStringSharedPref(key: String, value: String){
        with(sharedPreferences.edit()){
            putString(key, value)
                .commit()
        }
    }

    fun saveToBooleanSharedPref(key: String, value: Boolean){
        with(sharedPreferences.edit()){
            putBoolean(key, value)
                .commit()
        }
    }

    fun getStringFromSharedPref(key: String) : String? {
        return sharedPreferences.getString(key, null)
    }

    fun getBooleanFromSharedPref(key: String) : Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    fun removeFromSharedPref(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    fun removeAccessTokens(){
        with(sharedPreferences.edit()){
            remove(Constants.KEY_TOKEN)
                .apply()
        }
    }

}
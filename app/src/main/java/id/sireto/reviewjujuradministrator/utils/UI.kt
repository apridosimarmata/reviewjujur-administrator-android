package id.sireto.reviewjujuradministrator.utils

import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.snackbar.Snackbar
import id.sireto.reviewjujur.utils.Constants
import id.sireto.reviewjujuradministrator.models.Meta

object UI {
    fun snackbarTop(view: View, msg : String){
        val snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_SHORT)
        val view = snackbar.view
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity =  Gravity.TOP
        view.layoutParams = params
        snackbar.show()
    }

    fun snackbar(view: View, msg : String){
        Snackbar.make(
            view, msg, Snackbar.LENGTH_SHORT
        ).show()
    }

    fun showSnackbarByResponseCode(meta : Meta, view: View){
        when(meta.code){
            in 400 .. 450 -> meta.message?.let { snackbar(view, it) }
            in 500 .. 550 -> {
                if(meta.message!=null){
                    snackbarTop(view, meta.message!!)
                }else{
                    snackbarTop(view, Constants.SERVER_ERROR)
                }
            }
            else -> snackbarTop(view, Constants.UNKNOWN_ERROR)
        }
    }
}
package com.mahmoudbashir.realstate.Utils

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.mahmoudbashir.realstate.R

object Constants {
    const val IMAGE_REQUEST=1


     val mlist = arrayOf(
            R.drawable.home_1,
            R.drawable.home_1,
            R.drawable.home_1,
            R.drawable.home_1,
            R.drawable.home_1
    )

    fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        if (imm != null) imm.hideSoftInputFromWindow(
            view.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}
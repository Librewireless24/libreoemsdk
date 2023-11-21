package com.cumulations.libreV2

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.view.WindowInsets
import android.widget.TextView
import com.libreAlexa.R

/**
 * Created By Shaik Mansoor
 * 02/05/2023
 */
class CustomProgressDialog(context: Context) {
    private var dialog: CustomDialog
    private var cpTitle: TextView
    fun start(title: String = "") {
        cpTitle.text = title
        dialog.setCancelable(false)

        if( !dialog.isShowing)
        dialog.show()
    }

    fun stop() {
        if (dialog != null && dialog.isShowing) {
            dialog.dismiss()
        }
    }

    init {
        val inflater = (context as Activity).layoutInflater
        val view = inflater.inflate(R.layout.progress_bar, null)

        cpTitle = view.findViewById(R.id.txt_Message)
        // Custom Dialog initialization
        dialog = CustomDialog(context)
        dialog.setContentView(view)
    }

    class CustomDialog(context: Context) : Dialog(context) {
        init { // Set Semi-Transparent Color for Dialog Background Very IMP
            window?.decorView?.rootView?.setBackgroundResource(R.color.transparent)
            window?.decorView?.setOnApplyWindowInsetsListener { view, windowInsets ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { //One Plus 31 R=30 31 is greater than 30
                    view.rootWindowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars()).bottom
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //Moto=27 SAMSUNG=28 M=23 27&28 is greater than 23
                    view.rootWindowInsets?.stableInsetTop ?: 0
                }
                windowInsets

            }
        }
    }

}
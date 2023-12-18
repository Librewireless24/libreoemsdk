package com.cumulations.libreV2

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import com.libreAlexa.R

/**
 * Created By SHIAK MANSOOR
 * 20/SEPTEMBER/2023
 */
class ProgressButtonImageView @JvmOverloads constructor(context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {

    private val progressBar: ProgressBar
    private var imageView: ImageView

    init {
        val root = LayoutInflater.from(context).inflate(R.layout.progress_button, this, true)
        imageView = root.findViewById(R.id.imageView)
        progressBar = root.findViewById(R.id.progress_Bar)
        loadAttr(attrs, defStyleAttr)
    }

    private fun loadAttr(attrs: AttributeSet?, defStyleAttr: Int) {
        val arr = context.obtainStyledAttributes(attrs, R.styleable.ProgressButton, defStyleAttr, 0)

        val imageView = arr.getInt(R.styleable.ProgressButton_imageButtonStyle, 0)
        val loading = arr.getBoolean(R.styleable.ProgressButton_loading, false)
        val enabled = arr.getBoolean(R.styleable.ProgressButton_enabled, true)
        arr.recycle()
        isEnabled = enabled
        setImageResource(imageView)
        setLoading(loading)
    }

    fun setLoading(loading: Boolean) {
        isClickable = !loading //Disable clickable when loading
        if (loading) {
            imageView.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        } else {
            imageView.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }
    }

    fun setImageResource(image: Int?) {
        imageView.setImageResource(image!!)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        imageView.isEnabled = enabled
    }
}
package com.example.template_flo

import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.template_flo.databinding.CustomSnackbarBinding
import com.google.android.material.snackbar.Snackbar

class CustomSnackbar(view: View, private val message: String) {

    companion object {

        fun make(view: View, message: String) = CustomSnackbar(view, message)
    }

    private val context = view.context
    private val snackbar = Snackbar.make(view, "", 5000)
    private val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout

    private val inflater = LayoutInflater.from(context)
    private val binding: CustomSnackbarBinding = DataBindingUtil.inflate(
        inflater,
        R.layout.custom_snackbar,
        snackbarLayout,
        false
    )

    init {
        initView()
        initData()
    }

    private fun initView() {
        with(snackbarLayout) {
            removeAllViews()
            setPadding(0, 0, 0, 0)
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
            addView(binding.root, 0)
        }
    }

    private fun initData() {
        binding.customSnackbarTv.text = message
    }

    fun show() {
        snackbar.show()
    }
}
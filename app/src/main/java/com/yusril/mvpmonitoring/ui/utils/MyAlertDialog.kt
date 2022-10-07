package com.yusril.mvpmonitoring.ui.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yusril.mvpmonitoring.R
import com.yusril.mvpmonitoring.databinding.ErrorAlertDialogBinding

class MyAlertDialog {

    private lateinit var errorAlertDialogBinding: ErrorAlertDialogBinding
    private lateinit var errorAlertDialogView: View

    fun setActionAlertDialog(
        context: Context,
        title: String,
        setPositiveButton: String,
        onPositiveAction: () -> Unit
    ) : AlertDialog {
        return MaterialAlertDialogBuilder(context, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
            .setBackground(ResourcesCompat.getDrawable(context.resources, R.drawable.bg_white_rounded, null))
            .setTitle(title)
            .setPositiveButton(setPositiveButton) { _, _ ->
                onPositiveAction()
            }
            .create()
    }

    fun setErrorAlertDialog(
        context: Context,
        title: String,
        message: String,
        setPositiveButton: String,
        onPositiveAction: () -> Unit
    ) : AlertDialog {
        errorAlertDialogBinding = ErrorAlertDialogBinding
            .inflate(LayoutInflater.from(context), null, false)
        errorAlertDialogView = errorAlertDialogBinding.root
        errorAlertDialogBinding.apply {
            errDialogTitle.text = title
            errDialogDescription.text = message
        }

        return MaterialAlertDialogBuilder(context)
            .setView(errorAlertDialogView)
            .setBackground(ResourcesCompat.getDrawable(context.resources, R.drawable.bg_white_rounded, null))
            .setPositiveButton(setPositiveButton) { _, _ ->
                onPositiveAction()
            }
            .create()
    }
}
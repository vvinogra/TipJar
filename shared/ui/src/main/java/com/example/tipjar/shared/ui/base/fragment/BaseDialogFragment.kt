package com.example.tipjar.shared.ui.base.fragment

import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController

abstract class BaseDialogFragment(layoutId: Int) : DialogFragment(layoutId) {
    protected open fun onBackPressed(): Boolean {
        return findNavController().navigateUp()
    }
}
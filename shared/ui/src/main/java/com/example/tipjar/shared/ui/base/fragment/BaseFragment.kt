package com.example.tipjar.shared.ui.base.fragment

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

abstract class BaseFragment(layoutId: Int) : Fragment(layoutId) {
    protected open fun onBackPressed(): Boolean {
        return findNavController().navigateUp()
    }
}
package com.example.tipjar.shared.ui.base.fragment

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

abstract class BaseFragment(layoutId: Int) : Fragment(layoutId) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listenToSystemBackButton()
    }

    private fun listenToSystemBackButton() {
        requireActivity().onBackPressedDispatcher.addCallback(this) { onBackPressed() }
    }

    protected open fun onBackPressed(): Boolean {
        return findNavController().navigateUp()
    }
}
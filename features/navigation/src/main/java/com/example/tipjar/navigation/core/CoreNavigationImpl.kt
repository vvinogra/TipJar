package com.example.tipjar.navigation.core

import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.Navigator
import androidx.navigation.fragment.findNavController
import com.example.tipjar.core.navigation.CoreNavigation
import com.example.tipjar.core.ui.tipsplitter.TipSplitterFragment
import com.example.tipjar.core.ui.tipsplitter.TipSplitterFragmentDirections
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class CoreNavigationImpl @Inject constructor(): CoreNavigation {
    override fun fromTipSplitterToTipHistory(tipSplitterFragment: TipSplitterFragment) {
        val action = TipSplitterFragmentDirections.actionTipSplitterFragmentToTipHistoryFragment()

        tipSplitterFragment.navigate(action)
    }
}

fun Fragment.navigate(action: NavDirections) {
    findNavController().navigate(action)
}

fun Fragment.navigate(action: NavDirections, navigatorExtras: Navigator.Extras) {
    findNavController().navigate(action, navigatorExtras)
}
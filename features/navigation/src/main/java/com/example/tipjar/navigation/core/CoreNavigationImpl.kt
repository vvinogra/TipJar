package com.example.tipjar.navigation.core

import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.Navigator
import androidx.navigation.fragment.findNavController
import com.example.tipjar.core.navigation.CoreNavigation
import com.example.tipjar.core.ui.tipdetails.navigation.TipDetailsNavValues
import com.example.tipjar.core.ui.tiphistory.TipHistoryFragment
import com.example.tipjar.core.ui.tiphistory.TipHistoryFragmentDirections
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

    override fun fromTipHistoryToTipDetails(
        tipHistoryFragment: TipHistoryFragment,
        tipDetailsNavValues: TipDetailsNavValues
    ) {
        val action = TipHistoryFragmentDirections.actionTipHistoryFragmentToTipDetailsFragment(
            tipDetailsNavValues
        )

        tipHistoryFragment.navigate(action)
    }
}

fun Fragment.navigate(action: NavDirections) {
    findNavController().safeNavigate(action)
}

fun Fragment.navigate(action: NavDirections, navigatorExtras: Navigator.Extras) {
    findNavController().safeNavigate(action, navigatorExtras)
}

fun NavController.safeNavigate(direction: NavDirections) {
    currentDestination?.getAction(direction.actionId)?.run { navigate(direction) }
}

fun NavController.safeNavigate(
    direction: NavDirections,
    navigatorExtras: Navigator.Extras
) {
    currentDestination?.getAction(direction.actionId)?.run { navigate(direction, navigatorExtras) }
}
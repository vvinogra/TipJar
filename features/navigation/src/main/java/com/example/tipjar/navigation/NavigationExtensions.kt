package com.example.tipjar.navigation

import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.Navigator
import androidx.navigation.fragment.findNavController

fun Fragment.navigate(action: NavDirections) {
    findNavController().safeNavigate(action)
}

fun Fragment.navigate(action: NavDirections, navigatorExtras: Navigator.Extras) {
    findNavController().safeNavigate(action, navigatorExtras)
}

private fun NavController.safeNavigate(direction: NavDirections) {
    currentDestination?.getAction(direction.actionId)?.run { navigate(direction) }
}

private fun NavController.safeNavigate(
    direction: NavDirections,
    navigatorExtras: Navigator.Extras
) {
    currentDestination?.getAction(direction.actionId)?.run { navigate(direction, navigatorExtras) }
}
package com.example.testapplt.ui.common

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.testapplt.MainActivity
import com.example.testapplt.R
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.github.terrakok.cicerone.androidx.FragmentScreen
import kotlinx.coroutines.FlowPreview



class CustomNavigator(
    activity: MainActivity,
    containerID: Int
): AppNavigator(activity, containerID) {


    override fun setupFragmentTransaction(
        screen: FragmentScreen,
        fragmentTransaction: FragmentTransaction,
        currentFragment: Fragment?,
        nextFragment: Fragment
    ) {
        super.setupFragmentTransaction(
            screen,
            fragmentTransaction.setCustomAnimations(
                R.anim.slide_in_left,
                R.anim.slide_out_right,
                R.anim.slide_in_right,
                R.anim.slide_out_left
            ),
            currentFragment,
            nextFragment)
    }

}
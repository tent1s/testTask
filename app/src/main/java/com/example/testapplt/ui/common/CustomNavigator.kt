package com.example.testapplt.ui.common

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.testapplt.MainActivity
import com.example.testapplt.R
import kotlinx.coroutines.FlowPreview
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import ru.terrakok.cicerone.commands.Command

@FlowPreview
class CustomNavigator(
    activity: MainActivity,
    containerID: Int
): SupportAppNavigator(activity, containerID) {

    override fun setupFragmentTransaction(
        command: Command,
        currentFragment: Fragment?,
        nextFragment: Fragment?,
        fragmentTransaction: FragmentTransaction
    ) {
        super.setupFragmentTransaction(
            command, currentFragment, nextFragment,
            fragmentTransaction.setCustomAnimations(
                R.anim.slide_in_left,
                R.anim.slide_out_right,
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        )
    }

}
package com.example.testapplt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.testapplt.ui.common.CustomNavigator
import com.example.testapplt.ui.screen.SearchBooksScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Replace
import javax.inject.Inject

@FlowPreview
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    private val navigator by lazy {
        CustomNavigator(this, R.id.container)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            navigator.applyCommands(arrayOf<Command>(Replace(SearchBooksScreen("None"))))
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

}
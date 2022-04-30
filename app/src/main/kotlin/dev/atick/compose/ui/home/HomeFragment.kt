package dev.atick.compose.ui.home

import androidx.compose.runtime.Composable
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.atick.compose.ui.theme.ComposeTheme
import dev.atick.core.ui.BaseComposeFragment

@AndroidEntryPoint
class HomeFragment : BaseComposeFragment() {

    @Composable
    override fun ComposeUi() {
        ComposeTheme {
            HomeScreen(::navigateToConnectionFragment)
        }
    }

    private fun navigateToConnectionFragment() {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToConnectionFragment()
        )
    }
}
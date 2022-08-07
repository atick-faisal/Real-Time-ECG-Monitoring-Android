package dev.atick.compose.ui.login

import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.atick.compose.ui.theme.ComposeTheme
import dev.atick.core.ui.BaseComposeFragment
import dev.atick.core.utils.extensions.observeEvent

@AndroidEntryPoint
class LoginFragment : BaseComposeFragment() {

    private val viewModel: LoginViewModel by viewModels()

    @Composable
    override fun ComposeUi() {
        ComposeTheme {
            LoginScreen(::navigateToConnectionFragment)
        }
    }

    override fun observeStates() {
        super.observeStates()
        observeEvent(viewModel.loginState) {
            if (it == LoginState.LOGIN_SUCCESSFUL) {
                navigateToConnectionFragment()
            }
        }
    }

    private fun navigateToConnectionFragment() {
        findNavController().navigate(
            LoginFragmentDirections.actionLoginFragmentToConnectionFragment()
        )
    }
}
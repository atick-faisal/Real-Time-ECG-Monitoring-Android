package dev.atick.compose.ui.login

import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.atick.compose.ui.theme.ComposeTheme
import dev.atick.core.ui.BaseComposeFragment
import dev.atick.core.utils.extensions.collectWithLifecycle
import dev.atick.core.utils.extensions.observeEvent
import dev.atick.core.utils.extensions.showToast
import dev.atick.network.repository.CardiacZoneRepository
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseComposeFragment() {

    private val viewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var cardiacZoneRepository: CardiacZoneRepository

    @Composable
    override fun ComposeUi() {
        ComposeTheme {
            LoginScreen()
        }
    }

    override fun observeStates() {
        super.observeStates()
        observeEvent(viewModel.userId) { id ->
            if (id.length > 8) navigateToConnectionFragment()
        }

        collectWithLifecycle(cardiacZoneRepository.error) { error ->
            error?.message?.let {
                requireContext().showToast(it)
            }
        }
    }

    private fun navigateToConnectionFragment() {
        findNavController().navigate(
            LoginFragmentDirections.actionLoginFragmentToConnectionFragment()
        )
    }
}
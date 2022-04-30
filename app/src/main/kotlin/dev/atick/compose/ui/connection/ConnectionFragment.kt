package dev.atick.compose.ui.connection

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.atick.compose.ui.BleViewModel
import dev.atick.compose.ui.theme.ComposeTheme
import dev.atick.core.ui.BaseComposeFragment
import dev.atick.core.utils.extensions.observe
import dev.atick.core.utils.extensions.observeEvent
import dev.atick.core.utils.extensions.showToast

@AndroidEntryPoint
@ExperimentalMaterialApi
class ConnectionFragment : BaseComposeFragment() {

    private val viewModel: BleViewModel by viewModels()

    @Composable
    override fun ComposeUi() {
        ComposeTheme {
            ConnectionScreen()
        }
    }

    override fun observeStates() {
        super.observeStates()
        observeEvent(viewModel.connectionStatus) { status ->
            status?.let {
                requireContext().showToast(it)
            }
        }

        observeEvent(viewModel.isConnected) {
            if (it) navigateToHomeFragment()
        }
    }

    private fun navigateToHomeFragment() {
        findNavController().navigate(
            ConnectionFragmentDirections.actionConnectionFragmentToHomeFragment()
        )
    }
}
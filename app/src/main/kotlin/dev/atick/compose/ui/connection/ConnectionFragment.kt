package dev.atick.compose.ui.connection

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
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

        observe(viewModel.averageHeartRate) {
            requireContext().showToast("HR: $it")
        }
    }
}
package dev.atick.compose.ui.connection

import android.content.Intent
import android.os.Build
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.atick.compose.ui.BleViewModel
import dev.atick.compose.ui.theme.ComposeTheme
import dev.atick.core.service.BaseLifecycleService.Companion.ACTION_START_SERVICE
import dev.atick.core.ui.BaseComposeFragment
import dev.atick.core.utils.extensions.observe
import dev.atick.core.utils.extensions.observeEvent
import dev.atick.core.utils.extensions.showToast
import dev.atick.movesense.service.MovesenseService

@AndroidEntryPoint
@ExperimentalMaterialApi
class ConnectionFragment : BaseComposeFragment() {

    private val viewModel: BleViewModel by viewModels()

    @Composable
    override fun ComposeUi() {
        ComposeTheme {
            ConnectionScreen(::startMovesenseService)
        }
    }

    override fun observeStates() {
        super.observeStates()
        observe(viewModel.connectionStatus) { status ->
            requireContext().showToast(status.name)
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

    private fun startMovesenseService(address: String) {
        val intent = Intent(requireContext(), MovesenseService::class.java)
            .apply {
                action = ACTION_START_SERVICE
                putExtra(MovesenseService.BT_DEVICE_ADDRESS_KEY, address)
            }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().startForegroundService(intent)
        } else {
            requireContext().startService(intent)
        }
    }
}
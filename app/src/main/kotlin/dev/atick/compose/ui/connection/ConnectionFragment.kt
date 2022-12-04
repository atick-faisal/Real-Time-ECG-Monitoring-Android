package dev.atick.compose.ui.connection

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.atick.compose.base.BaseLifecycleService
import dev.atick.compose.service.CardiacZoneService
import dev.atick.compose.ui.theme.ComposeTheme
import dev.atick.core.ui.BaseComposeFragment
import dev.atick.core.utils.extensions.collectWithLifecycle
import dev.atick.core.utils.extensions.showToast
import dev.atick.movesense.data.ConnectionState


@AndroidEntryPoint
class ConnectionFragment : BaseComposeFragment() {

    private val viewModel: ConnectionViewModel by viewModels()

    @Composable
    override fun ComposeUi() {
        ComposeTheme {
            ConnectionScreen(::startMovesenseService)
        }
    }

    override fun observeStates() {
        collectWithLifecycle(viewModel.connectionStatus) { status ->
            requireContext().showToast(getString(status.description))
            when (status) {
                ConnectionState.CONNECTING ->
                    viewModel.setConnecting()
                ConnectionState.CONNECTED -> {
                    viewModel.setConnected()
                    navigateToDashboardFragment()
                }
                ConnectionState.CONNECTION_FAILED -> {
                    viewModel.setConnectionFailed()
                }
                else -> Unit
            }
        }
    }

    private fun startMovesenseService(address: String) {
        val intent = Intent(requireContext(), CardiacZoneService::class.java)
            .apply {
                action = BaseLifecycleService.ACTION_START_SERVICE
                putExtra(CardiacZoneService.BT_DEVICE_ADDRESS_KEY, address)
            }
        requireContext().startForegroundService(intent)
    }

    private fun navigateToDashboardFragment() {
        findNavController().navigate(
            ConnectionFragmentDirections.actionConnectionFragmentToDashboardFragment()
        )
    }
}
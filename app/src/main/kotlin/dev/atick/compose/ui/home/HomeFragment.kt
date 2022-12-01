package dev.atick.compose.ui.home

import android.content.Intent
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.atick.compose.R
import dev.atick.compose.ui.BleViewModel
import dev.atick.compose.ui.theme.ComposeTheme
import dev.atick.core.service.BaseLifecycleService.Companion.ACTION_STOP_SERVICE
import dev.atick.core.ui.BaseComposeFragment
import dev.atick.core.utils.extensions.observe
import dev.atick.core.utils.extensions.showAlertDialog
import dev.atick.movesense.service.MovesenseService

@AndroidEntryPoint
class HomeFragment : BaseComposeFragment() {

    private val viewModel: BleViewModel by viewModels()

    @Composable
    override fun ComposeUi() {
        ComposeTheme {
            HomeScreen(::onExitClick)
        }
    }

    override fun observeStates() {
        super.observeStates()
        observe(viewModel.ecgSignal) {
            viewModel.updateRecording(it)
        }

        observe(viewModel.isConnected) {
            if (!it) navigateToConnectionFragment()
        }
    }

    private fun onExitClick() {
        requireContext().showAlertDialog(
            title = getString(R.string.warning),
            message = getString(R.string.exit_alert_text),
            positiveText = getString(R.string.keep),
            negativeText = getString(R.string.exit),
            onApprove = { exitApp() },
            onCancel = {
                viewModel.disconnect()
                stopMovesenseService()
            }
        )
    }

    private fun stopMovesenseService() {
        val intent = Intent(requireContext(), MovesenseService::class.java)
            .apply {
                action = ACTION_STOP_SERVICE
            }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().startForegroundService(intent)
        } else {
            requireContext().startService(intent)
        }
    }

    private fun navigateToConnectionFragment() {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToConnectionFragment()
        )
    }

    private fun exitApp() {
        requireActivity().finishAffinity()
    }
}
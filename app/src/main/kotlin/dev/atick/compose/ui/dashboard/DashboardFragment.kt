package dev.atick.compose.ui.dashboard

import android.content.Intent
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.atick.compose.R
import dev.atick.compose.service.CardiacZoneService
import dev.atick.compose.ui.theme.ComposeTheme
import dev.atick.core.service.BaseLifecycleService
import dev.atick.core.ui.BaseComposeFragment
import dev.atick.core.utils.extensions.collectWithLifecycle
import dev.atick.core.utils.extensions.observeEvent
import dev.atick.core.utils.extensions.showAlertDialog
import dev.atick.core.utils.extensions.showToast
import dev.atick.movesense.Movesense
import dev.atick.movesense.data.ConnectionState
import javax.inject.Inject

@AndroidEntryPoint
class DashboardFragment : BaseComposeFragment() {

    @Inject
    lateinit var movesense: Movesense

    private val viewModel: DashboardViewModel by viewModels()

    @Composable
    override fun ComposeUi() {
        ComposeTheme {
            DashboardScreen(::showExitDialog)
        }
    }

    override fun observeStates() {
        collectWithLifecycle(movesense.connectionState) { state ->
            requireContext().showToast(getString(state.description))
            when (state) {
                ConnectionState.DISCONNECTED ->
                    navigateToConnectionFragment()
                ConnectionState.CONNECTION_FAILED ->
                    navigateToConnectionFragment()
                else -> Unit
            }
        }

        observeEvent(viewModel.connectDoctorStatus) {
            requireContext().showToast(it)
        }
    }

    private fun showExitDialog() {
        requireContext().showAlertDialog(
            title = getString(R.string.warning),
            message = getString(R.string.exit_alert_text),
            positiveText = getString(R.string.keep),
            negativeText = getString(R.string.exit),
            onApprove = { exitApp() },
            onCancel = {
                stopMovesenseService()
            }
        )
    }

    private fun stopMovesenseService() {
        val intent = Intent(requireContext(), CardiacZoneService::class.java)
            .apply {
                action = BaseLifecycleService.ACTION_STOP_SERVICE
            }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().startForegroundService(intent)
        } else {
            requireContext().startService(intent)
        }
    }

    private fun navigateToConnectionFragment() {
        findNavController().navigate(
            DashboardFragmentDirections.actionDashboardFragmentToConnectionFragment2()
        )
    }

    private fun exitApp() {
        requireActivity().finishAffinity()
    }

}
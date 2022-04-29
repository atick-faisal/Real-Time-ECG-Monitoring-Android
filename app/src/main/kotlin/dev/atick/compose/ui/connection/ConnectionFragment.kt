package dev.atick.compose.ui.connection

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import dagger.hilt.android.AndroidEntryPoint
import dev.atick.compose.ui.theme.ComposeTheme
import dev.atick.core.ui.BaseComposeFragment

@AndroidEntryPoint
@ExperimentalMaterialApi
class ConnectionFragment : BaseComposeFragment() {
    @Composable
    override fun ComposeUi() {
        ComposeTheme {
            ConnectionScreen()
        }
    }
}
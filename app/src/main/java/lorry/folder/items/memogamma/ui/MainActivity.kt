package lorry.folder.items.memogamma.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import lorry.folder.items.memogamma.bubble.BubbleManager
import lorry.folder.items.memogamma.ui.theme.MemoGammaTheme

class MainActivity : ComponentActivity() {

    private var hasStartedBubble = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MemoGammaTheme {
                // Ici tu peux montrer un logo, un message ou rien du tout
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (!hasStartedBubble) {
            hasStartedBubble = true

            if (Settings.canDrawOverlays(this)) {
                // Affiche la bulle maintenant que l'activité est "vivante"
                BubbleManager.showBubble(this)

                // Termine l'activité proprement, sans fuite
                window.decorView.postDelayed({ finish() }, 500)
            } else {
                askOverlayPermission(this)
            }
        }
    }

    private fun askOverlayPermission(context: Context) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:lorry.folder.items.memogamma")
        )
        context.startActivity(intent)
    }
}

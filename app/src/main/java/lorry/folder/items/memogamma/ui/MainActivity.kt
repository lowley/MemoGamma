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
import androidx.compose.ui.Modifier
import lorry.folder.items.memogamma.bubble.BubbleManager
import lorry.folder.items.memogamma.ui.theme.MemoGammaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MemoGammaTheme {
                askOverlayPermission(this)
                BubbleManager.showBubble(this)
                finish()
            }
        }
    }

    fun askOverlayPermission(context: Context) {
        if (!Settings.canDrawOverlays(context)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:lorry.folder.items.memogamma")
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            return
        }
    }


}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier.Companion) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}


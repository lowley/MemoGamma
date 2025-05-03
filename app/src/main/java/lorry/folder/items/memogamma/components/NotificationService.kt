package lorry.folder.items.memogamma.components

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.renderscript.RenderScript
import androidx.core.app.NotificationCompat
import lorry.folder.items.memogamma.R
import lorry.folder.items.memogamma.bubble.BubbleManager

class NotificationService : Service() {

    companion object {
        const val CHANNEL_ID = "memo_bubble_channel"
        const val ACTION_SHOW_BUBBLE = "ACTION_SHOW_BUBBLE"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("NotificationService.onStartCommand: ${intent?.action}")
        println("BubbleManager: $BubbleManager")
        when (intent?.action) {
            ACTION_SHOW_BUBBLE -> {
                BubbleManager.showBubble(this)
            }
        }

        startForeground(1, createNotification())
        return START_STICKY
    }

    private fun createNotification(): Notification {
        val showBubbleIntent = Intent(this, NotificationService::class.java).apply {
            action = ACTION_SHOW_BUBBLE
        }
        val pendingIntent = PendingIntent.getService(
            this, 0, showBubbleIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("MemoGamma actif")
            .setContentText("Touchez pour afficher la bulle")
            .setSmallIcon(R.drawable.palette) // ton icône
            .addAction(R.drawable.palette, "Afficher bulle", pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

}

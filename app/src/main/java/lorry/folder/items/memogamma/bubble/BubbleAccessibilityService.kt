package lorry.folder.items.memogamma.bubble

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

class BubbleAccessibilityService: AccessibilityService() {
    
    companion object{
        var currentPackage: String? = null
        var targetPackage = "idm.internet.download.manager.plus"
        var targetDrawing: String? = null
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
//        println("onAccessibilityEvent: ${event.packageName}")
        currentPackage = event.packageName?.toString()

        when (currentPackage) {
            targetPackage -> {
                try{
//                    println("onAccessibilityEvent: $targetDrawing, $targetPackage")
                    if (targetDrawing != null) {
                        BubbleManager.setState(targetDrawing!!)
                    }
                    BubbleManager.displayBubble()
                } catch(ex: Exception) {
                    println("onAccessibilityEvent: BubbleManager pas prêt")
                }
            }
            
            else -> {
//                BubbleManager.hide()
            }
        }
    }

    override fun onInterrupt() {}
}

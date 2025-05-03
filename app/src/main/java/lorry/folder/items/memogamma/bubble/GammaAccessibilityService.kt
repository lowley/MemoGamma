package lorry.folder.items.memogamma.bubble

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import lorry.folder.items.memogamma.__data.userPreferences.UserPreferences

class GammaAccessibilityService : AccessibilityService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var isInTargetPackage = false
    private var previousPackage: String? = null

    companion object {
        var currentPackage: String? = null
        var targetPackage = "idm.internet.download.manager.plus"
        var targetDrawing: String? = null
    }

    override fun onServiceConnected() {
        super.onServiceConnected()

        serviceScope.launch {
            val userPreferences = UserPreferences(this@GammaAccessibilityService)

            launch {
                userPreferences.reactivePackage.collect {
                    targetPackage = it
                }
            }

            launch {
                userPreferences.drawingToLoad.collect {
                    targetDrawing = it
                }
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val newPackage = event.packageName?.toString() ?: return
        if (newPackage == previousPackage) return // Évite les doublons

        previousPackage = newPackage
        currentPackage = newPackage

        if (newPackage == targetPackage) {
            // Entrée dans le package cible
            if (!isInTargetPackage) {
                isInTargetPackage = true

                try {
                    //BubbleManager.displayBubble()
                    println("onAccessibilityEvent: ${BubbleManager}, $targetDrawing")
                    targetDrawing?.let {
                        BubbleManager.setState(it)
                    }
                } catch (ex: Exception) {
                    println("onAccessibilityEvent: BubbleManager pas prêt")
                }
            }
        } else {
            // Sortie du package cible
            if (isInTargetPackage) {
                isInTargetPackage = false
                BubbleManager.resetState()
                //BubbleManager.displayBubble()
            }
        }
    }

    override fun onInterrupt() {}
}

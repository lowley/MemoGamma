package lorry.folder.items.memogamma.bubble

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import lorry.folder.items.memogamma.__data.userPreferences.UserPreferences
import lorry.folder.items.memogamma.components.dataClasses.AlarmClock

class GammaAccessibilityService : AccessibilityService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var isInTargetPackage = false
    private var previousPackage: String? = null

    companion object {
        var currentPackage: String? = null
        var alarmClocks: Set<AlarmClock> = setOf()
    }

    override fun onServiceConnected() {
        super.onServiceConnected()

        serviceScope.launch {
            val userPreferences = UserPreferences(this@GammaAccessibilityService)

            launch {
                userPreferences.alarmClocks.collect {
                    alarmClocks = it
                }
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val newPackage = event.packageName?.toString() ?: return
        currentPackage = newPackage
        if (newPackage == previousPackage) return // Évite les doublons

        previousPackage = newPackage

        if (newPackage == "com.samsung.android.service.aircommand"
            || newPackage == "lorry.folder.items.memogamma"
        )
            return

        val matchingAlarmClock = alarmClocks.firstOrNull { alarmClock ->
            alarmClock.realPackage == newPackage
        }

        if (matchingAlarmClock != null) {
            // Entrée dans un package cible
            if (!isInTargetPackage) {
                isInTargetPackage = true

                try {
                    //BubbleManager.displayBubble()
                    println("onAccessibilityEvent: ${BubbleManager}, ${matchingAlarmClock.sheet}")
                    BubbleManager.setState(matchingAlarmClock.sheet)
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

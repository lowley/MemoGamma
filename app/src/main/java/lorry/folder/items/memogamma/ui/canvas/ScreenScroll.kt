package lorry.folder.items.memogamma.ui.canvas

import android.view.MotionEvent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset

class ScreenScroll {
    private var startPoint: Offset? = null
    private var endPoint: Offset? = null

    // Translation totale persistante
    var totalTranslation by mutableStateOf(Offset.Zero)
        private set

    // Translation temporaire en cours de geste
    val currentDelta: Offset
        get() = if (startPoint != null && endPoint != null) {
            endPoint!! - startPoint!!
        } else Offset.Zero

    val visualOffset: Offset
        get() = totalTranslation + currentDelta

    private var internalVisualOffset = Offset.Zero

    var visualOffsetState by mutableStateOf(Offset.Zero)
        private set

    fun getOffsetForCalculation(): Offset = internalVisualOffset
    
    fun onTouchEvent(event: MotionEvent): Boolean {
        val isOneFinger = event.pointerCount == 1 &&
                event.getToolType(0) == MotionEvent.TOOL_TYPE_FINGER

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                if (isOneFinger) {
                    startPoint = Offset(event.x, event.y)
                    endPoint = Offset(event.x, event.y)
                    internalVisualOffset = totalTranslation + currentDelta
                    visualOffsetState = internalVisualOffset
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (isOneFinger) {
                    endPoint = Offset(event.x, event.y)
                    visualOffsetState = totalTranslation + currentDelta
                }
            }

            MotionEvent.ACTION_UP -> {
                if (startPoint != null && endPoint != null) {
                    val delta = endPoint!! - startPoint!!
                    totalTranslation += delta
                }
                startPoint = null
                endPoint = null
                internalVisualOffset = totalTranslation
                visualOffsetState = internalVisualOffset
            }

            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_POINTER_UP -> {
                startPoint = null
                endPoint = null
            }
        }
        return true
    }

    fun reset() {
        startPoint = null
        endPoint = null
    }
}

package lorry.folder.items.memogamma.bubble

import android.content.Context
import android.os.Build
import android.view.MotionEvent
import androidx.compose.ui.graphics.Path
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import lorry.folder.items.memogamma.bubble.BubbleManager.intentChannel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class BubbleViewModel @Inject constructor(
    @ApplicationContext val context: Context,
) : ViewModel() {
    val Id: UUID = UUID.randomUUID()

    private val _bubbleState = MutableStateFlow(BubbleState.BUBBLE)
    val bubbleState: StateFlow<BubbleState> = _bubbleState

    var coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    private var _stylusState = MutableStateFlow(StylusState())
    val stylusState: StateFlow<StylusState> = _stylusState

    private var currentPath = mutableListOf<DrawPoint>()

    private fun requestRendering(stylusState: StylusState) {
        // Updates the stylusState, which triggers a flow.
        _stylusState.value = stylusState
    }

    private fun createPath(): Path {
        val path = Path()

        for (point in currentPath) {
            if (point.type == DrawPointType.START) {
                path.moveTo(point.x, point.y)
            } else {
                path.lineTo(point.x, point.y)
            }
        }
        return path
    }

    fun processMotionEvent(motionEvent: MotionEvent): Boolean {
        println("GAMMA: Event reçu : ${motionEvent.actionMasked}, x=${motionEvent.x}, y=${motionEvent.y}")
        
        when (motionEvent.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                currentPath.add(
                    DrawPoint(motionEvent.x, motionEvent.y, DrawPointType.START)
                )
            }
            MotionEvent.ACTION_MOVE -> {
                currentPath.add(DrawPoint(motionEvent.x, motionEvent.y, DrawPointType.LINE))
            }
            MotionEvent.ACTION_UP -> {
                val canceled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                        (motionEvent.flags and MotionEvent.FLAG_CANCELED) == MotionEvent.FLAG_CANCELED

                if(canceled) {
                    cancelLastStroke()
                } else {
                    currentPath.add(DrawPoint(motionEvent.x, motionEvent.y, DrawPointType.LINE))
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                // Unwanted touch detected.
                cancelLastStroke()
            }
            else -> return false
        }

        requestRendering(
            StylusState(
                tilt = motionEvent.getAxisValue(MotionEvent.AXIS_TILT),
                pressure = motionEvent.pressure,
                orientation = motionEvent.orientation,
                path = createPath()
            )
        )

        return true
    }
    

    fun setBubbleState(value: BubbleState) {
        _bubbleState.value = value
    }

    fun create() {
        println("THOO: create lancé...")
    }

    private fun cancelLastStroke() {
        // Find the last START event.
        val lastStart = currentPath.last {
            it.type == DrawPointType.START
        }
        val lastIndex = currentPath.indexOf(lastStart)

        // If found, keep the element from 0 until the very last event before the last MOVE event.
        if (lastIndex > 0) {
            currentPath = currentPath.subList(0, lastIndex - 1)
        }
    }
    
    init {
        println("THOO: init() exécutée...")
        create()

        viewModelScope.launch {
            intentChannel.consumeAsFlow().collect { intent ->
                when (intent) {
                    is BubbleIntent.ShowTotalDialog -> {
                        _bubbleState.value = BubbleState.TOTAL
                    }

                    is BubbleIntent.ShowBubbleDialog -> {
                        _bubbleState.value = BubbleState.BUBBLE
                    }

                    is BubbleIntent.HideBubbleDialog -> {
                        _bubbleState.value = BubbleState.HIDDEN
                    }
                }
            }
        }
    }
}

sealed class BubbleIntent {
    object ShowTotalDialog : BubbleIntent()
    object ShowBubbleDialog : BubbleIntent()
    object HideBubbleDialog : BubbleIntent()
}

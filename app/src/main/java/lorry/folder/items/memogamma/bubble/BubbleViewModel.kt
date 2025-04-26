package lorry.folder.items.memogamma.bubble

import android.content.Context
import android.os.Build
import android.view.MotionEvent
import androidx.compose.ui.graphics.Color
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
import kotlinx.coroutines.flow.update
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

    private val _pointerCount = MutableStateFlow(0)
    val pointerCount: StateFlow<Int> = _pointerCount

    private val _activePointer = MutableStateFlow(0)
    val activePointer: StateFlow<Int> = _activePointer

    private val _pointerName1 = MutableStateFlow("")
    val pointerName1: StateFlow<String> = _pointerName1

    private val _pointerName2 = MutableStateFlow("")
    val pointerName2: StateFlow<String> = _pointerName2

    private val _action = MutableStateFlow("")
    val pointerAction: StateFlow<String> = _action

    private val _pointerAction2 = MutableStateFlow("")
    val pointerAction2: StateFlow<String> = _pointerAction2

    private val _stylusColor = MutableStateFlow(Color.Black)
    val stylusColor: StateFlow<Color> = _stylusColor

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
        _pointerCount.update { motionEvent.pointerCount }
        _activePointer.value = motionEvent.actionIndex;
        _action.value = when (motionEvent.actionMasked) {
            MotionEvent.ACTION_MOVE -> "ACTION_MOVE"
            MotionEvent.ACTION_DOWN -> "ACTION_DOWN"
            MotionEvent.ACTION_UP -> "ACTION_UP"
            MotionEvent.ACTION_POINTER_DOWN -> "ACTION_POINTER_DOWN"
            MotionEvent.ACTION_POINTER_UP -> "ACTION_POINTER_UP"
            else -> "Inconnu"
        }

        if (motionEvent.pointerCount >= 1) {
            _pointerName1.value = when (motionEvent.getToolType(0)) {
                MotionEvent.TOOL_TYPE_STYLUS -> "0-Stylus"
                MotionEvent.TOOL_TYPE_FINGER -> "0-Doigt"
                MotionEvent.TOOL_TYPE_MOUSE -> "0-Souris"
                MotionEvent.TOOL_TYPE_ERASER -> "0-Crayon"
                else -> "0-Inconnu"
            }
        } else {
            _pointerName1.value = ""
            _pointerName2.value = ""
        }
        if (motionEvent.pointerCount >= 2)
            _pointerName2.value = when (motionEvent.getToolType(1)) {
                MotionEvent.TOOL_TYPE_STYLUS -> "1-Stylus"
                MotionEvent.TOOL_TYPE_FINGER -> "1-Doigt"
                MotionEvent.TOOL_TYPE_MOUSE -> "1-Souris"
                MotionEvent.TOOL_TYPE_ERASER -> "1-Crayon"
                else -> "1-Inconnu"
            }
        else {
            _pointerName2.value = ""
        }

        println("GAMMA: Event reçu : ${motionEvent.actionMasked}, x=${motionEvent.x}, y=${motionEvent.y}")

        when (motionEvent.actionMasked) {
            MotionEvent.ACTION_MOVE -> {
                if (motionEvent.pointerCount >= 2 &&
                    motionEvent.getToolType(0) == MotionEvent.TOOL_TYPE_FINGER
                    && motionEvent.getToolType(1) == MotionEvent.TOOL_TYPE_FINGER
                ) {
                    println("GAMMA : Scroll à 2 doigts détecté")
                    TwoFingersScrollState.setEndPoint(
                        DrawPoint(motionEvent.x, motionEvent.y, DrawPointType.START)
                    )

                    currentPath = currentPath.map {
                        it.copy(
                            x = it.x + (TwoFingersScrollState.deltaX ?: 0f),
                            y = it.y + (TwoFingersScrollState.deltaY ?: 0f)
                        )
                    }.toMutableList()

                    requestRendering(
                        StylusState(
                            tilt = motionEvent.getAxisValue(MotionEvent.AXIS_TILT),
                            pressure = motionEvent.pressure,
                            orientation = motionEvent.orientation,
                            path = createPath()
                        )
                    )

                    TwoFingersScrollState.setStartPoint(
                        DrawPoint(motionEvent.x, motionEvent.y, DrawPointType.START)
                    )
                }
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                //2e pointeur abaissé
                println("GAMMA pointer down")
                TwoFingersScrollState.setStartPoint(
                    DrawPoint(motionEvent.x, motionEvent.y, DrawPointType.START)
                )
            }

            MotionEvent.ACTION_POINTER_UP -> {
                //2e pointeur est levé
                println("GAMMA pointer up")
                TwoFingersScrollState.reset()
            }

            MotionEvent.ACTION_UP -> {
                //1er pointeur est levé
                println("GAMMA up")
                _pointerCount.value = 0
            }

            MotionEvent.ACTION_CANCEL -> {
                println("GAMMA cancel")
                _pointerCount.value = 0
            }
        }

        //main ou doigt
        val stylusIndex = (0..motionEvent.pointerCount - 1).firstOrNull {
            motionEvent.getToolType(it) == MotionEvent.TOOL_TYPE_STYLUS
        } ?: -1

        if (stylusIndex == -1)
            return true
        else
        //stylet
        {
            if (motionEvent.actionIndex != stylusIndex)
                return true

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

                    if (canceled) {
                        cancelLastStroke()
                    } else {
                        currentPath.add(DrawPoint(motionEvent.x, motionEvent.y, DrawPointType.LINE))
                    }
                }

                MotionEvent.ACTION_CANCEL -> {
                    // Unwanted touch detected.
                    cancelLastStroke()
                }

                else -> return true
            }
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

    fun setStylusColor(color: Color) {
        _stylusColor.value = color
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

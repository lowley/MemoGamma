package lorry.folder.items.memogamma.ui.canvas

import android.graphics.PointF
import android.view.MotionEvent
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.core.view.MotionEventCompat.ACTION_HOVER_ENTER
import androidx.core.view.MotionEventCompat.ACTION_HOVER_EXIT
import androidx.core.view.MotionEventCompat.ACTION_HOVER_MOVE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import lorry.folder.items.memogamma.components.dataClasses.DrawPoint
import lorry.folder.items.memogamma.components.dataClasses.DrawPointType
import lorry.folder.items.memogamma.components.dataClasses.StylusState
import lorry.folder.items.memogamma.components.dataClasses.StylusStatePath
import lorry.folder.items.memogamma.components.dataClasses.TwoFingersScrollState
import lorry.folder.items.memogamma.components.extensions.createPath
import lorry.folder.items.memogamma.undoRedo.DrawingsUndoRedo
import lorry.folder.items.memogamma.undoRedo.UndoRedoManager
import javax.inject.Inject

//👆 DOIGT EN BAS       ─▶ startPoint = (x0, y0)
//─▶ internalVisualOffset = totalTranslation
//─▶ visualOffsetState = totalTranslation
//
//👉 DOIGT GLISSE       ─▶ endPoint = (x1, y1)
//─▶ currentDelta = end - start
//─▶ internalVisualOffset = totalTranslation + currentDelta
//─▶ visualOffsetState = totalTranslation + currentDelta
//⎡
//🎨 CANVAS             ────────────────────────────────────────────────┘
//└▶ `translate(visualOffsetState.x, visualOffsetState.y)`
//← décalage visuel appliqué à tout le dessin
//
//✋ DOIGT LEVÉ         ─▶ delta = end - start
//─▶ totalTranslation += delta
//─▶ visualOffsetState = totalTranslation
//
//✍ STYLET EN BAS      ─▶ motionEvent.x, y (brut)
//└▶ DrawPoint(x, y)
//⎡
//🎨 CANVAS             ─────────────────────────────────┘
//└▶ `translate(...)` fait en sorte que le point
//soit affiché au bon endroit (sous le stylet)

class ScreenInteraction @Inject constructor(

) {
    private val _stylusColor = MutableStateFlow(Color.Companion.Black)
    val stylusColor: StateFlow<Color> = _stylusColor

    private val _stylusStroke = MutableStateFlow(Stroke(width = 1f))
    val stylusStroke: StateFlow<Stroke> = _stylusStroke

    private var _initialStylusState =
        MutableStateFlow(StylusState(StylusState.Companion.DEFAULT.name))
    val initialStylusState: StateFlow<StylusState> = _initialStylusState

    private var _currentStylusState =
        MutableStateFlow(StylusState(StylusState.Companion.DEFAULT.name))
    val currentStylusState: StateFlow<StylusState> = _currentStylusState

    var lastStateBeforeStylusDown: StylusState? = null

    var translationState by mutableStateOf(TwoFingersScrollState(null, null))
    val translateX = derivedStateOf { (translationState.xvar ?: 0f) + totalTranslation.x }
    val translateY = derivedStateOf { (translationState.yvar ?: 0f) + totalTranslation.y }

    var totalTranslation = PointF(0f, 0f)
    val screenScroll = ScreenScroll()

    val visualOffset: Offset
        get() = screenScroll.visualOffset

    val totalOffset: Offset
        get() = screenScroll.totalTranslation

    var x by mutableFloatStateOf(0f)
    var y by mutableFloatStateOf(0f)
    
    ///////////////////////////////
    // flows > actions sur flows //
    ///////////////////////////////

    private fun requestRendering(stylusState: StylusState) {
        _currentStylusState.value = stylusState
    }

    fun setStylusStroke(stroke: Stroke) {
        _stylusStroke.value = Stroke(stroke.width)
    }

    fun setInitialStylusState(state: StylusState) {
        _currentStylusState.value = state
    }

    fun setCurrentStylusState(state: StylusState) {
        _currentStylusState.value = state
    }

    fun setStylusColor(color: Color) {
        _stylusColor.value = color
    }

    fun setState(state: StylusState) {
        TwoFingersScrollState.Companion.reset()

        _currentStylusState.value = state
        _initialStylusState.value = state
    }

    ////////////////////////////////
    // actions sur flows > métier //
    ////////////////////////////////

    fun resetScroll() {
        screenScroll.reset()
    }

    fun processScrollEvent(event: MotionEvent): Boolean {
        return screenScroll.onTouchEvent(event)
    }

    fun processMotionEvent(motionEvent: MotionEvent): Boolean {

        val offset = screenScroll.totalTranslation
        val correctedX = motionEvent.x - offset.x
        val correctedY = motionEvent.y - offset.y
        x = motionEvent.x
        y = motionEvent.y
        
        //updateDebugIndicators(motionEvent)

        //main ou doigt
        val stylusIndex = (0..motionEvent.pointerCount - 1).firstOrNull {
            motionEvent.getToolType(it) == MotionEvent.TOOL_TYPE_STYLUS
        } ?: -1

        if (motionEvent.actionMasked == ACTION_HOVER_ENTER
            || motionEvent.actionMasked == ACTION_HOVER_MOVE
            || motionEvent.actionMasked == ACTION_HOVER_EXIT
        )
            return true

        if (stylusIndex == -1)
            return true
        else
        //stylet
        {
            if (motionEvent.actionIndex != stylusIndex)
                return true

            when (motionEvent.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    lastStateBeforeStylusDown = currentStylusState.value

                    var newItems: MutableList<StylusStatePath> = mutableListOf()
                    for (item in currentStylusState.value.items) {
                        newItems.add(item)
                    }
                    newItems.add(
                        StylusStatePath(
                            path = mutableListOf(
                                DrawPoint(
                                    correctedX,
                                    correctedY,
                                    DrawPointType.START
                                )
                            ).createPath(),
                            color = stylusColor.value,
                            style = stylusStroke.value,
                            pointList = mutableListOf(
                                DrawPoint(
                                    correctedX,
                                    correctedY,
                                    DrawPointType.START
                                )
                            )
                        )
                    )

                    val newState = StylusState(currentStylusState.value.name, newItems)

                    _currentStylusState.update { state -> newState }
                }

                MotionEvent.ACTION_MOVE -> {
                    _currentStylusState.update { state ->
                        val items = state.items.toMutableList()
                        val lastItem = items.last()

                        val points = lastItem.pointList.toMutableList()
                        points.add(
                            DrawPoint(correctedX, correctedY, DrawPointType.LINE)
                        )

                        val newPath = Path().apply {
                            addPath(lastItem.path)
                            lineTo(correctedX, correctedY)
                        }
                        items.add(lastItem.copy(path = newPath, pointList = points))
                        StylusState(state.name, items)
                    }
                }

                MotionEvent.ACTION_UP -> {
                    val canceled = true &&
                            (motionEvent.flags and MotionEvent.FLAG_CANCELED) == MotionEvent.FLAG_CANCELED

                    if (canceled) {
                        cancelLastStroke()
                    } else {
                        var newItems: MutableList<StylusStatePath> = mutableListOf()
                        for (item in currentStylusState.value.items.dropLast(1)) {
                            newItems.add(item)
                        }

                        val lastItem = currentStylusState.value.items.last()
                        lastItem.path.lineTo(correctedX, correctedY)
                        val newPointList = lastItem.pointList
                            .toMutableList().plus(
                                DrawPoint(
                                    correctedX,
                                    correctedY,
                                    DrawPointType.LINE
                                )
                            )
                        val newLastItem = lastItem.copy(pointList = newPointList)
                        val newState = StylusState(
                            currentStylusState.value.name,
                            newItems.plus(newLastItem).toMutableList()
                        )

                        if (lastStateBeforeStylusDown != null)
                            UndoRedoManager.add(
                                DrawingsUndoRedo(lastStateBeforeStylusDown!!, newState, this)
                            )
                        lastStateBeforeStylusDown = null

                        _currentStylusState.update { state ->
                            newState
                        }
                    }
                }

                MotionEvent.ACTION_CANCEL -> {
                    // Unwanted touch detected.
                    cancelLastStroke()
                }

                else -> return true
            }
        }

        requestRendering(currentStylusState.value)

        return true
    }

    private fun fingerTranslation(motionEvent: MotionEvent) {
        val oneFinger = motionEvent.pointerCount == 1 &&
                motionEvent.getToolType(0) == MotionEvent.TOOL_TYPE_FINGER

        when (motionEvent.actionMasked) {
            MotionEvent.ACTION_MOVE -> {
                if (oneFinger) {
                    //Scroll à 1 doigt détecté"
                    translationState = translationState.copy(
                        endPoint = DrawPoint(motionEvent.x, motionEvent.y, DrawPointType.LINE)
                    )
                }

                requestRendering(currentStylusState.value)
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                //2e pointeur abaissé
                translationState = translationState.copy(
                    startPoint = DrawPoint(motionEvent.x, motionEvent.y, DrawPointType.START)
                )
            }

            MotionEvent.ACTION_POINTER_UP -> {
                //2e pointeur est levé
                translationState = translationState.copy(
                    startPoint = null,
                    endPoint = null
                )
            }

            MotionEvent.ACTION_DOWN -> {
                if (oneFinger) {
                    //2e pointeur abaissé
                    translationState = translationState.copy(
                        startPoint = DrawPoint(motionEvent.x, motionEvent.y, DrawPointType.START)
                    )
                }
            }

            MotionEvent.ACTION_UP -> {
                if (oneFinger) {
                    val dx = translationState.xvar ?: 0f
                    val dy = translationState.yvar ?: 0f

                    totalTranslation.x += dx
                    totalTranslation.y += dy

                    translationState = translationState.copy(
                        startPoint = null,
                        endPoint = null
                    )
                } else {
                    //1er pointeur est levé
                    //setPointerCount(0)
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                //setPointerCount(0)
            }
        }
    }

    private fun cancelLastStroke() {
        // Find the last START event.
        _currentStylusState.update { state ->
            state.apply {
                if (items.size >= 1)
                    items.mapIndexedNotNull { i, item ->
                        if (i == items.size - 1)
                            null
                        else item
                    }
            }
        }
    }

    //    private val _pointerCount = MutableStateFlow(0)
//    val pointerCount: StateFlow<Int> = _pointerCount
//
//    private val _activePointer = MutableStateFlow(0)
//    val activePointer: StateFlow<Int> = _activePointer
//
//    private val _pointerName1 = MutableStateFlow("")
//    val pointerName1: StateFlow<String> = _pointerName1
//
//    private val _pointerName2 = MutableStateFlow("")
//    val pointerName2: StateFlow<String> = _pointerName2
//
//    private val _action = MutableStateFlow("")
//    val pointerAction: StateFlow<String> = _action
//
//    private val _pointerAction2 = MutableStateFlow("")
//    val pointerAction2: StateFlow<String> = _pointerAction2
//
//    fun setPointerCount(value: Int) {
//        _pointerCount.value = value
//    }
//    
//    private fun updateDebugIndicators(motionEvent: MotionEvent) {
//        _pointerCount.update { motionEvent.pointerCount }
//        _activePointer.value = motionEvent.actionIndex;
//        _action.value = when (motionEvent.actionMasked) {
//            MotionEvent.ACTION_MOVE -> "ACTION_MOVE"
//            MotionEvent.ACTION_DOWN -> "ACTION_DOWN"
//            MotionEvent.ACTION_UP -> "ACTION_UP"
//            MotionEvent.ACTION_POINTER_DOWN -> "ACTION_POINTER_DOWN"
//            MotionEvent.ACTION_POINTER_UP -> "ACTION_POINTER_UP"
//            else -> "Inconnu"
//        }
//
//        if (motionEvent.pointerCount >= 1) {
//            _pointerName1.value = when (motionEvent.getToolType(0)) {
//                MotionEvent.TOOL_TYPE_STYLUS -> "0-Stylus"
//                MotionEvent.TOOL_TYPE_FINGER -> "0-Doigt"
//                MotionEvent.TOOL_TYPE_MOUSE -> "0-Souris"
//                MotionEvent.TOOL_TYPE_ERASER -> "0-Crayon"
//                else -> "0-Inconnu"
//            }
//        } else {
//            _pointerName1.value = ""
//            _pointerName2.value = ""
//        }
//        if (motionEvent.pointerCount >= 2)
//            _pointerName2.value = when (motionEvent.getToolType(1)) {
//                MotionEvent.TOOL_TYPE_STYLUS -> "1-Stylus"
//                MotionEvent.TOOL_TYPE_FINGER -> "1-Doigt"
//                MotionEvent.TOOL_TYPE_MOUSE -> "1-Souris"
//                MotionEvent.TOOL_TYPE_ERASER -> "1-Crayon"
//                else -> "1-Inconnu"
//            }
//        else {
//            _pointerName2.value = ""
//        }
//    }
}
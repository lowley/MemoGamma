package lorry.folder.items.memogamma.ui

import android.os.Build
import android.view.MotionEvent
import androidx.compose.ui.graphics.Path
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import lorry.folder.items.memogamma.components.dataClasses.DrawPoint
import lorry.folder.items.memogamma.components.dataClasses.DrawPointType
import lorry.folder.items.memogamma.components.dataClasses.StylusState
import lorry.folder.items.memogamma.components.dataClasses.StylusStatePath
import lorry.folder.items.memogamma.components.dataClasses.TwoFingersScrollState
import lorry.folder.items.memogamma.components.extensions.createPath
import lorry.folder.items.memogamma.components.extensions.translate
import lorry.folder.items.memogamma.undoRedo.DrawingsUndoRedo
import lorry.folder.items.memogamma.undoRedo.UndoRedoManager
import javax.inject.Inject

class ScreenInteraction @Inject constructor(
    
) {
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
    
    
    
    fun processMotionEvent(motionEvent: MotionEvent): Boolean {

        //updateDebugIndicators(motionEvent)

        fingerTranslation(motionEvent)

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
                    lastStateBeforeStylusDown = currentStylusState.value

                    var newItems: MutableList<StylusStatePath> = mutableListOf()
                    for (item in currentStylusState.value.items) {
                        newItems.add(item)
                    }
                    newItems.add(
                        StylusStatePath(
                            path = mutableListOf(
                                DrawPoint(motionEvent.x, motionEvent.y, DrawPointType.START)
                            ).createPath(),
                            color = stylusColor.value,
                            style = stylusStroke.value,
                            pointList = mutableListOf(
                                DrawPoint(motionEvent.x, motionEvent.y, DrawPointType.START)
                            )
                        )
                    )

                    val newState = StylusState(currentStylusState.value.name, newItems)

                    _currentStylusState.update { state ->
                        newState
                    }
                }

                MotionEvent.ACTION_MOVE -> {
                    _currentStylusState.update { state ->
                        val items = state.items.toMutableList()
                        val lastItem = items.last()

                        val points = lastItem.pointList.toMutableList()
                        points.add(DrawPoint(motionEvent.x, motionEvent.y, DrawPointType.LINE))

                        val newPath = Path().apply {
                            addPath(lastItem.path)
                            lineTo(motionEvent.x, motionEvent.y)
                        }
                        items.add(lastItem.copy(path = newPath, pointList = points))
                        StylusState(state.name, items)
                    }
                }

                MotionEvent.ACTION_UP -> {
                    val canceled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                            (motionEvent.flags and MotionEvent.FLAG_CANCELED) == MotionEvent.FLAG_CANCELED

                    if (canceled) {
                        cancelLastStroke()
                    } else {
                        var newItems: MutableList<StylusStatePath> = mutableListOf()
                        for (item in currentStylusState.value.items.dropLast(1)) {
                            newItems.add(item)
                        }

                        val lastItem = currentStylusState.value.items.last()
                        lastItem.path.lineTo(motionEvent.x, motionEvent.y)
                        val newPointList = lastItem.pointList
                            .toMutableList().plus(
                                DrawPoint(motionEvent.x, motionEvent.y, DrawPointType.LINE)
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
                    TwoFingersScrollState.addPoint(motionEvent)

                    _currentStylusState.update { state ->
                        val newItems = state.items.translate(TwoFingersScrollState)

                        StylusState(state.name, newItems)
                    }
                }

                requestRendering(currentStylusState.value)
                TwoFingersScrollState.setStartPoint(motionEvent)
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                //2e pointeur abaissé
                TwoFingersScrollState.setStartPoint(motionEvent)
            }

            MotionEvent.ACTION_POINTER_UP -> {
                //2e pointeur est levé
                TwoFingersScrollState.reset()
            }

            MotionEvent.ACTION_DOWN -> {
                if (oneFinger) {
                    //2e pointeur abaissé
                    TwoFingersScrollState.setStartPoint(motionEvent)
                }
            }

            MotionEvent.ACTION_UP -> {
                if (oneFinger) {
                    TwoFingersScrollState.reset()
                } else {
                    //1er pointeur est levé
                    setPointerCount(0)
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                setPointerCount(0)
            }
        }
    }


    private fun updateDebugIndicators(motionEvent: MotionEvent) {
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
    }
}
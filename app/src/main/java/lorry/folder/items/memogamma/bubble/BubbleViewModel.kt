package lorry.folder.items.memogamma.bubble

import android.content.Context
import android.os.Build
import android.view.MotionEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lorry.folder.items.memogamma.__data.userPreferences.IUserPreferences
import lorry.folder.items.memogamma.bubble.BubbleManager.intentChannel
import lorry.folder.items.memogamma.undoRedo.DrawingsUndoRedo
import lorry.folder.items.memogamma.undoRedo.UndoRedoManager
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class BubbleViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    val userPreferences: IUserPreferences
) : ViewModel() {

    companion object {
        fun createPath(points: MutableList<DrawPoint>): Path {
            val path = Path()

            for (point in points) {
                if (point.type == DrawPointType.START) {
                    path.moveTo(point.x, point.y)
                } else {
                    path.lineTo(point.x, point.y)
                }
            }
            return path
        }
    }

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

    private val _stylusStroke = MutableStateFlow(Stroke(width = 1f))
    val stylusStroke: StateFlow<Stroke> = _stylusStroke

    var coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var _initialStylusState = MutableStateFlow(
        StylusState(
            StylusState.DEFAULT.name
        )
    )
    val initialStylusState: StateFlow<StylusState> = _initialStylusState

    private var _currentStylusState = MutableStateFlow(StylusState(StylusState.DEFAULT.name))
    val currentStylusState: StateFlow<StylusState> = _currentStylusState

    private val _persistencePopupVisible = MutableStateFlow(false)
    val persistencePopupVisible: StateFlow<Boolean> = _persistencePopupVisible

    private val _alarmClockPopupPopupVisible = MutableStateFlow(false)
    val alarmClockPopupPopupVisible: StateFlow<Boolean> = _alarmClockPopupPopupVisible
    
    var lastStateBeforeStylusDown: StylusState? = null

    val drawings = userPreferences.sheets

    private val _recomposePersistencePopupTrigger = MutableStateFlow(false)
    val recomposePersistencePopupTrigger: StateFlow<Boolean> = _recomposePersistencePopupTrigger

    private val _recomposeAlarmClockPopupTrigger = MutableStateFlow(false)
    val recomposeAlarmClockPopupTrigger: StateFlow<Boolean> = _recomposeAlarmClockPopupTrigger

    private fun requestRendering(stylusState: StylusState) {
        _currentStylusState.value = stylusState
    }

    val alarmClockEnabled = combine(userPreferences.drawingToLoad, userPreferences.reactivePackage){ drawingToLoad, reactivePackage ->
        drawingToLoad.isNotEmpty() && reactivePackage.isNotEmpty()
    }
    

    fun changeRecomposePersistencePopupTrigger() {
        _recomposePersistencePopupTrigger.value = !recomposePersistencePopupTrigger.value
    }

    fun changeRecomposeAlarmClockPopupTrigger() {
        _recomposeAlarmClockPopupTrigger.value = !recomposeAlarmClockPopupTrigger.value
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
                if (motionEvent.pointerCount == 1 &&
                    motionEvent.getToolType(0) == MotionEvent.TOOL_TYPE_FINGER
                ) {
                    println("GAMMA : Scroll à 1 doigt détecté")
                    TwoFingersScrollState.setEndPoint(
                        DrawPoint(motionEvent.x, motionEvent.y, DrawPointType.START)
                    )

                    _currentStylusState.update { state ->
                        val newItems = state.items.map { item ->
                            val newPath = Path().apply {
                                addPath(item.path)
                                transform(Matrix().apply {
                                    translate(
                                        TwoFingersScrollState.deltaX ?: 0f,
                                        TwoFingersScrollState.deltaY ?: 0f
                                    )
                                })
                            }
                            item.copy(path = newPath)
                        }.toMutableList()

                        StylusState(state.name, newItems)
                    }
                }

                requestRendering(
                    currentStylusState.value
                )

                TwoFingersScrollState.setStartPoint(
                    DrawPoint(motionEvent.x, motionEvent.y, DrawPointType.START)
                )
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

            MotionEvent.ACTION_DOWN -> {
                if (motionEvent.pointerCount == 1 &&
                    motionEvent.getToolType(0) == MotionEvent.TOOL_TYPE_FINGER
                ) {
                    //2e pointeur abaissé
                    println("GAMMA pointer down")
                    TwoFingersScrollState.setStartPoint(
                        DrawPoint(motionEvent.x, motionEvent.y, DrawPointType.START)
                    )
                }
            }

//            MotionEvent.ACTION_UP -> {
//                //2e pointeur est levé
//                println("GAMMA pointer up")
//                TwoFingersScrollState.reset()
//            }

            MotionEvent.ACTION_UP -> {
                if (motionEvent.pointerCount == 1 &&
                    motionEvent.getToolType(0) == MotionEvent.TOOL_TYPE_FINGER
                ) {
                    TwoFingersScrollState.reset()
                } else {
                    //1er pointeur est levé
                    println("GAMMA up")
                    _pointerCount.value = 0
                }
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
                    lastStateBeforeStylusDown = currentStylusState.value

                    var newItems: MutableList<StylusStatePath> = mutableListOf()
                    for (item in currentStylusState.value.items) {
                        newItems.add(item)
                    }
                    newItems.add(
                        StylusStatePath(
                            path = createPath(
                                mutableListOf(
                                    DrawPoint(motionEvent.x, motionEvent.y, DrawPointType.START)
                                )
                            ),
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


    fun setBubbleState(value: BubbleState) {
        _bubbleState.value = value
    }

    fun create() {
        println("THOO: create lancé...")
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

    fun setStylusColor(color: Color) {
        _stylusColor.value = color
    }

    fun saveCurrentStateAs(name: String, replace: Boolean = false) {
        viewModelScope.launch {
            val updated = currentStylusState.value.copy(name = name)
            if (replace)
                userPreferences.update_sheet(updated)
            else
                userPreferences.add_sheet(updated)
        }
    }

    fun setState(state: StylusState) {
        TwoFingersScrollState.reset()

        _currentStylusState.value = state
        _initialStylusState.value = state
    }

    fun setPersistencePopupVisible(value: Boolean) {
        _persistencePopupVisible.value = value
    }
    
    fun setAlarmClockPopupVisible(value: Boolean) {
        _alarmClockPopupPopupVisible.value = value
    }

    fun deleteDrawing(state: StylusState) {
        viewModelScope.launch {
            userPreferences.remove_sheet(state)
        }
    }

    fun setAwaking(state: StylusState) {
        val targetPackage = GammaAccessibilityService.currentPackage
        if (targetPackage == null)
            return

        viewModelScope.launch(Dispatchers.IO) {
            userPreferences.setReactivePackage(targetPackage)
            userPreferences.setDrawingToLoad(state.name)
        }

        GammaAccessibilityService.targetPackage = targetPackage
        GammaAccessibilityService.targetDrawing = state.name
    }

    init {
        println("THOO: init() exécutée...")
        create()

        viewModelScope.launch {
            intentChannel.consumeAsFlow<BubbleIntent>().collect { intent: BubbleIntent ->
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

                    is BubbleIntent.OpenDrawing -> {
                        val value = drawings.first()
                        val drawing =
                            value.firstOrNull() { drawing ->
                                drawing.name == intent.name
                            }
                        if (drawing != null) {
                            setState(drawing)
                            setPersistencePopupVisible(false)
                            changeRecomposePersistencePopupTrigger()
                        }
                        if (intent.name == StylusState.DEFAULT.name) {
                            setState(StylusState.DEFAULT)
                            setPersistencePopupVisible(false)
                            changeRecomposePersistencePopupTrigger()
                        }
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
    data class OpenDrawing(val name: String) : BubbleIntent()
}

fun Path.translate(dx: Float, dy: Float): Path {
    val matrix = Matrix()
    matrix.translate(dx, dy)
    this.transform(matrix)
    return this
}

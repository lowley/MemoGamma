package lorry.folder.items.memogamma.bubble

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lorry.folder.items.memogamma.__data.userPreferences.IUserPreferences
import lorry.folder.items.memogamma.bubble.BubbleManager.intentChannel
import lorry.folder.items.memogamma.components.dataClasses.AlarmClock
import lorry.folder.items.memogamma.components.dataClasses.BubbleIntent
import lorry.folder.items.memogamma.components.dataClasses.StylusState
import lorry.folder.items.memogamma.components.dataClasses.TwoFingersScrollState
import lorry.folder.items.memogamma.ui.ScreenInteraction
import javax.inject.Inject

@HiltViewModel
class BubbleViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    val userPreferences: IUserPreferences,
    val screenInteraction: ScreenInteraction
) : ViewModel() {

    private val _bubbleState = MutableStateFlow(BubbleState.BUBBLE)
    val bubbleState: StateFlow<BubbleState> = _bubbleState

    private val _stylusColor = MutableStateFlow(Color.Black)
    val stylusColor: StateFlow<Color> = _stylusColor

    private val _stylusStroke = MutableStateFlow(Stroke(width = 1f))
    val stylusStroke: StateFlow<Stroke> = _stylusStroke

    private var _initialStylusState = MutableStateFlow(StylusState(StylusState.DEFAULT.name))
    val initialStylusState: StateFlow<StylusState> = _initialStylusState

    private var _currentStylusState = MutableStateFlow(StylusState(StylusState.DEFAULT.name))
    val currentStylusState: StateFlow<StylusState> = _currentStylusState

    private val _persistencePopupVisible = MutableStateFlow(false)
    val persistencePopupVisible: StateFlow<Boolean> = _persistencePopupVisible

    private val _alarmClockPopupVisible = MutableStateFlow(false)
    val alarmClockPopupVisible: StateFlow<Boolean> = _alarmClockPopupVisible

    private val _recomposePersistencePopupTrigger = MutableStateFlow(false)
    val recomposePersistencePopupTrigger: StateFlow<Boolean> = _recomposePersistencePopupTrigger

    private val _recomposeAlarmClockPopupTrigger = MutableStateFlow(false)
    val recomposeAlarmClockPopupTrigger: StateFlow<Boolean> = _recomposeAlarmClockPopupTrigger

    var lastStateBeforeStylusDown: StylusState? = null
    var coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    val drawings = userPreferences.sheets
    val alarmClocks = userPreferences.alarmClocks

    private fun requestRendering(stylusState: StylusState) {
        _currentStylusState.value = stylusState
    }

    val alarmClockEnabled = alarmClocks.map { alarmClocks -> alarmClocks.isNotEmpty() }
    val currentAlarmClocksFlow: StateFlow<Set<AlarmClock>>
        get() = alarmClocks.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = setOf()
        )
    val currentAlarmClocks: Set<AlarmClock>
        get() = currentAlarmClocksFlow.value

    ///////////////////////
    // actions sur flows //
    ///////////////////////

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
    
    fun setPointerCount(value: Int) {
        _pointerCount.value = value 
    }

    fun setBubbleState(value: BubbleState) {
        _bubbleState.value = value
    }

    fun setStylusColor(color: Color) {
        _stylusColor.value = color
    }

    fun setPersistencePopupVisible(value: Boolean) {
        _persistencePopupVisible.value = value
    }

    fun setAlarmClockPopupVisible(value: Boolean) {
        _alarmClockPopupVisible.value = value
    }

    fun setState(state: StylusState) {
        TwoFingersScrollState.reset()

        _currentStylusState.value = state
        _initialStylusState.value = state
    }
    
    //////////////
    // drawings //
    //////////////

    fun saveCurrentStateAs(state: StylusState, name: String, replace: Boolean = false) {
        viewModelScope.launch {
            if (replace)
                userPreferences.update_sheet(state)
            else
                userPreferences.add_sheet(state.copy(name = name))
        }
    }

    fun deleteDrawing(state: StylusState) {
        viewModelScope.launch {
            userPreferences.remove_sheet(state)
        }
    }

    fun replaceName(state: StylusState, newName: String) {
        viewModelScope.launch {
            userPreferences.replaceName(state, newName)
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
    
    //////////////////
    // alarm clocks //
    //////////////////

    fun deleteAlarmClock(clock: AlarmClock) {
        viewModelScope.launch(Dispatchers.IO) {
            userPreferences.removeAlarmClock(clock)
        }
    }

    fun setAwaking(state: StylusState) {
        val targetPackage = GammaAccessibilityService.currentPackage
        if (targetPackage == null)
            return

        viewModelScope.launch(Dispatchers.IO) {
            userPreferences.addAlarmClock(
                AlarmClock(targetPackage, targetPackage, state.name)
            )
        }
    }

    /////////////////////
    // méthodes métier //
    /////////////////////

    init {
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



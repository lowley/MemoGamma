package lorry.folder.items.memogamma.bubble

import android.content.Context
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
import kotlinx.coroutines.launch
import lorry.folder.items.memogamma.__data.userPreferences.IUserPreferences
import lorry.folder.items.memogamma.bubble.BubbleManager.intentChannel
import lorry.folder.items.memogamma.components.dataClasses.AlarmClock
import lorry.folder.items.memogamma.components.dataClasses.BubbleIntent
import lorry.folder.items.memogamma.components.dataClasses.StylusState
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

    private val _persistencePopupVisible = MutableStateFlow(false)
    val persistencePopupVisible: StateFlow<Boolean> = _persistencePopupVisible

    private val _alarmClockPopupVisible = MutableStateFlow(false)
    val alarmClockPopupVisible: StateFlow<Boolean> = _alarmClockPopupVisible

    private val _recomposePersistencePopupTrigger = MutableStateFlow(false)
    val recomposePersistencePopupTrigger: StateFlow<Boolean> = _recomposePersistencePopupTrigger

    private val _recomposeAlarmClockPopupTrigger = MutableStateFlow(false)
    val recomposeAlarmClockPopupTrigger: StateFlow<Boolean> = _recomposeAlarmClockPopupTrigger
    
    var coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    val drawings = userPreferences.sheets
    val alarmClocks = userPreferences.alarmClocks
    
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

    fun setBubbleState(value: BubbleState) {
        _bubbleState.value = value
    }

    fun setPersistencePopupVisible(value: Boolean) {
        _persistencePopupVisible.value = value
    }

    fun setAlarmClockPopupVisible(value: Boolean) {
        _alarmClockPopupVisible.value = value
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
                            screenInteraction.setState(drawing)
                            setPersistencePopupVisible(false)
                            changeRecomposePersistencePopupTrigger()
                        }
                        if (intent.name == StylusState.DEFAULT.name) {
                            screenInteraction.setState(StylusState.DEFAULT)
                            setPersistencePopupVisible(false)
                            changeRecomposePersistencePopupTrigger()
                        }
                    }
                }
            }
        }
    }
}



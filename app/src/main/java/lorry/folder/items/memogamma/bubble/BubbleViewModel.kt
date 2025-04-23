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

    fun setBubbleState(value: BubbleState) {
        _bubbleState.value = value
    }

    fun create() {
        println("THOO: create lancé...")

        LoadBubbleContent()
    }

    public fun LoadBubbleContent() {
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

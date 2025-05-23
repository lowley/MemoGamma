package lorry.folder.items.memogamma.bubble

import android.content.Context
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.github.only52607.compose.window.ComposeFloatingWindow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.first
import lorry.folder.items.memogamma.__data.userPreferences.UserPreferences
import lorry.folder.items.memogamma.components.VideoShortcutsBubbleViewModelFactory
import lorry.folder.items.memogamma.components.dataClasses.BubbleIntent
import lorry.folder.items.memogamma.components.dataClasses.StylusState
import lorry.folder.items.memogamma.ui.canvas.ScreenInteraction

object BubbleManager {
    private var floatingWindow: ComposeFloatingWindow? = null
    private lateinit var viewModel: BubbleViewModel

    val intentChannel = Channel<BubbleIntent>(Channel.UNLIMITED)
    var bubbleState: BubbleState = BubbleState.BUBBLE


    fun showBubble(context: Context) {
        if (floatingWindow == null) {
            val userPreferences = UserPreferences(context.applicationContext)
            val screenInteraction: ScreenInteraction = ScreenInteraction()
            floatingWindow = ComposeFloatingWindow(context.applicationContext).apply {
                setContent {
                    viewModel = ViewModelProvider(
                        ViewModelStore(),
                        VideoShortcutsBubbleViewModelFactory(
                            context.applicationContext, userPreferences, screenInteraction
                        )
                    )[BubbleViewModel::class.java]

                    var stylusState = viewModel.screenInteraction.currentStylusState
                    var drawings = userPreferences.sheets
                    
                    LaunchedEffect(Unit) {
                        intentChannel.consumeAsFlow().collect { intent ->
                            when (intent) {
                                is BubbleIntent.ShowTotalDialog -> viewModel.setBubbleState(
                                    BubbleState.TOTAL
                                )

                                is BubbleIntent.ShowBubbleDialog -> viewModel.setBubbleState(
                                    BubbleState.BUBBLE
                                )

                                is BubbleIntent.HideBubbleDialog -> viewModel.setBubbleState(
                                    BubbleState.HIDDEN
                                )

                                is BubbleIntent.OpenDrawing -> {
                                    val value = drawings.first()
                                    val drawing =
                                        value.firstOrNull() { drawing ->
                                            drawing.name == intent.name
                                        }
                                    if (drawing != null) {
                                        viewModel.screenInteraction.setState(drawing)
                                        viewModel.setPersistencePopupVisible(false)
                                        viewModel.changeRecomposePersistencePopupTrigger()
                                    }
                                    if (intent.name == StylusState.DEFAULT.name) {
                                        viewModel.screenInteraction.setState(StylusState.DEFAULT)
                                        viewModel.setPersistencePopupVisible(false)
                                        viewModel.changeRecomposePersistencePopupTrigger()
                                    }
                                }
                            }
                        }
                    }

                    LaunchedEffect(Unit) {
                        viewModel.bubbleState.collect { state ->
                            bubbleState = state
                        }
                    }

                    BubbleContent(viewModel)
                }
            }
        }
        floatingWindow?.show()
        intentChannel.trySend(BubbleIntent.ShowBubbleDialog)
    }

    fun hide() {
        intentChannel.trySend(BubbleIntent.HideBubbleDialog)
    }

    fun displayTotal() {
        intentChannel.trySend(BubbleIntent.ShowTotalDialog)
    }

    fun displayBubble() {
        intentChannel.trySend(BubbleIntent.ShowBubbleDialog)
    }
    
    fun setState(name: String){
        intentChannel.trySend(BubbleIntent.OpenDrawing(name))
    }
    
    fun resetState(){
        intentChannel.trySend(BubbleIntent.OpenDrawing(StylusState.DEFAULT.name))
    }

    fun toggleBubbleTotal() {
        val newState =
            when (bubbleState) {
                BubbleState.BUBBLE -> BubbleState.TOTAL
                BubbleState.TOTAL -> BubbleState.BUBBLE
                BubbleState.HIDDEN -> BubbleState.HIDDEN
            }
        intentChannel.trySend(
            when (newState) {
                BubbleState.BUBBLE -> BubbleIntent.ShowBubbleDialog
                BubbleState.TOTAL -> BubbleIntent.ShowTotalDialog
                BubbleState.HIDDEN -> BubbleIntent.HideBubbleDialog
            }
        )
    }

    fun showOrCreateOrToggle(context: Context) {
        val newState = when (bubbleState) {
            BubbleState.BUBBLE -> BubbleState.HIDDEN
            BubbleState.TOTAL -> BubbleState.HIDDEN
            BubbleState.HIDDEN -> BubbleState.BUBBLE
        }
        when (newState) {
            BubbleState.BUBBLE -> showBubble(context)
            BubbleState.TOTAL -> {
                showBubble(context)
                displayTotal()
            }

            BubbleState.HIDDEN -> hide()
        }
    }
}

enum class BubbleState {
    HIDDEN,
    BUBBLE,
    TOTAL
}

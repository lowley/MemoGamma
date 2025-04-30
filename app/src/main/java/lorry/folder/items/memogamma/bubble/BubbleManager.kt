package lorry.folder.items.memogamma.bubble

import android.content.Context
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.github.only52607.compose.window.ComposeFloatingWindow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import lorry.folder.items.memogamma.components.VideoShortcutsBubbleViewModelFactory

object BubbleManager {
    private var floatingWindow: ComposeFloatingWindow? = null
    private lateinit var viewModel: BubbleViewModel

    val intentChannel = Channel<BubbleIntent>(Channel.UNLIMITED)
    var bubbleState: BubbleState = BubbleState.BUBBLE


    fun showBubble(context: Context) {
        if (floatingWindow == null) {
            floatingWindow = ComposeFloatingWindow(context.applicationContext).apply {
                setContent {
                    viewModel = ViewModelProvider(
                        ViewModelStore(),
                        VideoShortcutsBubbleViewModelFactory(
                            context.applicationContext
                        )
                    )[BubbleViewModel::class.java]

                    var stylusState = viewModel.currentStylusState

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

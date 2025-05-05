package lorry.folder.items.memogamma.undoRedo

import androidx.compose.ui.graphics.Color
import lorry.folder.items.memogamma.bubble.BubbleViewModel

class StylusColorUndoRedo(
    override val oldState: Color, 
    override val newState: Color,
    val viewModel: BubbleViewModel
) : IUndoRedo<Color> {
    
    override fun doChange() {
        viewModel.screenInteraction.setStylusColor(newState)
    }

    override fun undoChange() {
        viewModel.screenInteraction.setStylusColor(oldState)
    }
}
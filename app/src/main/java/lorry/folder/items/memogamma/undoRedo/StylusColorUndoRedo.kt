package lorry.folder.items.memogamma.undoRedo

import androidx.compose.ui.graphics.Color
import lorry.folder.items.memogamma.bubble.BubbleViewModel

class StylusColorUndoRedo(
    override val oldState: Color, 
    override val newState: Color,
    val viewModel: BubbleViewModel
) : IUndoRedo<Color> {
    
    override fun doChange() {
        viewModel.setStylusColor(newState)
    }

    override fun undoChange() {
        viewModel.setStylusColor(oldState)
    }
}
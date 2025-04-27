package lorry.folder.items.memogamma.undoRedo

import androidx.compose.ui.graphics.Color
import lorry.folder.items.memogamma.bubble.BubbleViewModel
import javax.inject.Inject

class StylusColorChange(
    override val oldState: Color, 
    override val newState: Color,
    val viewModel: BubbleViewModel
) : IUndoRedoChange<Color> {
    
    override fun doChange() {
        viewModel.setStylusColor(newState)
    }

    override fun undoChange() {
        viewModel.setStylusColor(oldState)
    }
}
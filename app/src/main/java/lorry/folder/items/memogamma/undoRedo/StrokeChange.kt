package lorry.folder.items.memogamma.undoRedo

import androidx.compose.ui.graphics.drawscope.Stroke
import lorry.folder.items.memogamma.bubble.BubbleViewModel

class StrokeChange(
    override val oldState: Stroke, 
    override val newState: Stroke,
    val viewModel: BubbleViewModel)
    : IUndoRedoChange<Stroke> {
    
    override fun doChange() {
        viewModel.setStylusStroke(newState)
    }

    override fun undoChange() {
        viewModel.setStylusStroke(oldState)
    }
}
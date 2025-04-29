package lorry.folder.items.memogamma.undoRedo

import lorry.folder.items.memogamma.bubble.BubbleViewModel
import lorry.folder.items.memogamma.bubble.StylusState

class DrawingsUndoRedo(
    override val oldState: StylusState,
    override val newState: StylusState,
    val viewModel: BubbleViewModel
) : IUndoRedo<StylusState> {
    override fun doChange() {
        viewModel.setStylusState(newState.copyDeep())
    }

    override fun undoChange() {
        viewModel.setStylusState(oldState.copyDeep())
    }
}
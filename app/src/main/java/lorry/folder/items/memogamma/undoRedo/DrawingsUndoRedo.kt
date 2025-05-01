package lorry.folder.items.memogamma.undoRedo

import lorry.folder.items.memogamma.bubble.BubbleViewModel
import lorry.folder.items.memogamma.bubble.StylusStateDto

class DrawingsUndoRedo(
    override val oldState: StylusStateDto,
    override val newState: StylusStateDto,
    val viewModel: BubbleViewModel
) : IUndoRedo<StylusStateDto> {
    override fun doChange() {
        viewModel.setCurrentStylusState(newState.copyDeep())
    }

    override fun undoChange() {
        viewModel.setCurrentStylusState(oldState.copyDeep())
    }
}
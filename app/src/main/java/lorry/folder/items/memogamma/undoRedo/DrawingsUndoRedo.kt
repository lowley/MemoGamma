package lorry.folder.items.memogamma.undoRedo

import lorry.folder.items.memogamma.components.dataClasses.StylusState
import lorry.folder.items.memogamma.ui.canvas.ScreenInteraction

class DrawingsUndoRedo(
    override val oldState: StylusState,
    override val newState: StylusState,
    val screenInteraction: ScreenInteraction
) : IUndoRedo<StylusState> {
    override fun doChange() {
        screenInteraction.setCurrentStylusState(newState.copyDeep())
    }

    override fun undoChange() {
        screenInteraction.setCurrentStylusState(oldState.copyDeep())
    }
}
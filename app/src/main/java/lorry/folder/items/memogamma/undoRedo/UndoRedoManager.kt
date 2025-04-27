package lorry.folder.items.memogamma.undoRedo

import androidx.compose.ui.graphics.Color
import kotlin.math.ceil
import kotlin.math.floor

object UndoRedoManager {
    var items: List<IUndoRedoChange<*>> = emptyList()
    var position: Float = -1f
    val index: Int
        get() = (position + 0.5).toInt()

    fun currentPositionIsBeforeFirst(): Boolean{
        return position < 0
    }

    fun currentPositionIsAfterLast(): Boolean{
        return position > items.size - 1
    }
    
    fun undo() {
        if (position > 0f) {
            items.undoChange(position)
            position--
        }
    }

    fun redo() {
        if (position >= items.size - 1)
            return

        items.doChange(position)
        position++
    }

    fun add(change: IUndoRedoChange<*>) {
        if (position >= 0 && position < items.size - 1)
            items = items.subList(0, index)
        items += change
        position = items.size - 0.5f
    }
}

fun List<IUndoRedoChange<*>>.undoChange(position: Float) {
    val item = this[floor(position).toInt()]
    item.undoChange()
}

fun List<IUndoRedoChange<*>>.doChange(position: Float) {
    val item = this[ceil(position).toInt()]
    item.doChange()
}
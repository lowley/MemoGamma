package lorry.folder.items.memogamma.undoRedo

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.random.Random

object UndoRedoManager {
    var items: List<IUndoRedoChange<*>> = emptyList()
    var position: Float = -1f
    val index: Int
        get() = (position + 0.5).toInt()

    private val _changeNotifier = MutableStateFlow(0)
    val changeNotifier = _changeNotifier.asStateFlow()
    
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
            _changeNotifier.value = Random.nextInt(1_000_000_000)
        }
    }

    fun redo() {
        if (position >= items.size - 1)
            return

        items.doChange(position)
        position++
        _changeNotifier.value = Random.nextInt(1_000_000_000)
    }

    fun add(change: IUndoRedoChange<*>) {
        if (position >= 0 && position < items.size - 1)
            items = items.subList(0, index)
        items += change
        position = items.size - 0.5f
        _changeNotifier.value = Random.nextInt(1_000_000_000)
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
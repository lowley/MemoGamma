package lorry.folder.items.memogamma.undoRedo

interface IUndoRedoChange<T> {
    val oldState: T
    val newState: T
    
    fun doChange()
    fun undoChange()
}
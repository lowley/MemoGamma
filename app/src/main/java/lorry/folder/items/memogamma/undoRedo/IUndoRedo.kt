package lorry.folder.items.memogamma.undoRedo

interface IUndoRedo<T> {
    val oldState: T
    val newState: T
    
    fun doChange()
    fun undoChange()
}
package lorry.folder.items.memogamma.__data.userPreferences

import kotlinx.coroutines.flow.Flow
import lorry.folder.items.memogamma.bubble.StylusState

interface IUserPreferences {

    val sheetsDTO: Flow<Set<String>>
    val sheets: Flow<Set<StylusState>>
    
    suspend fun get_sheets(): Set<StylusState>
    suspend fun save_sheets(values: Set<StylusState>)
    suspend fun add_sheet(sheet: StylusState)
    suspend fun update_sheet(sheet: StylusState)
    suspend fun remove_sheet(sheet: StylusState)
    
    val drawingToLoad: Flow<String>
    val reactivePackage: Flow<String>
    
    suspend fun getDrawingToLoad(): String
    suspend fun setDrawingToLoad(value: String)
    suspend fun getReactivePackage(): String
    suspend fun setReactivePackage(value: String)

    
    
}
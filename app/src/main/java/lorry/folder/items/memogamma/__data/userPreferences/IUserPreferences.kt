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
}
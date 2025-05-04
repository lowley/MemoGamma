package lorry.folder.items.memogamma.__data.userPreferences

import kotlinx.coroutines.flow.Flow
import lorry.folder.items.memogamma.components.dataClasses.AlarmClock
import lorry.folder.items.memogamma.components.dataClasses.StylusState

interface IUserPreferences {

    val sheetsDTO: Flow<Set<String>>
    val sheets: Flow<Set<StylusState>>
    
    
    suspend fun get_sheets(): Set<StylusState>
    suspend fun save_sheets(values: Set<StylusState>)
    suspend fun add_sheet(sheet: StylusState)
    suspend fun update_sheet(sheet: StylusState)
    suspend fun remove_sheet(sheet: StylusState)

    val alarmClocksDTO: Flow<Set<String>>
    val alarmClocks: Flow<Set<AlarmClock>>
    
    suspend fun getAlarmClocks(): Set<AlarmClock>
    suspend fun save_alarmClocksDTO(alarmClocks: Set<String>)
    suspend fun addAlarmClock(alarmClock: AlarmClock)
    suspend fun replaceAlarmClock(alarmClock: AlarmClock)
    suspend fun removeAlarmClock(alarmClock: AlarmClock)
    suspend fun replaceName(state: StylusState, string: String)
}
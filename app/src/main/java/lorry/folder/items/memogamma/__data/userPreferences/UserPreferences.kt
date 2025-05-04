package lorry.folder.items.memogamma.__data.userPreferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import lorry.folder.items.memogamma.components.dataClasses.AlarmClock
import lorry.folder.items.memogamma.components.dataClasses.StylusState
import lorry.folder.items.memogamma.components.extensions.toAlarmClock
import lorry.folder.items.memogamma.components.extensions.toDTO
import lorry.folder.items.memogamma.components.extensions.toStylusState
import javax.inject.Singleton

val Context.dataStore by preferencesDataStore(name = "preferences_gamma")

@Singleton
open class UserPreferences @javax.inject.Inject constructor(private val context: Context) :
    IUserPreferences {

    private val dataStore = context.applicationContext.dataStore

    companion object {
        private val SHEETS_KEY = stringSetPreferencesKey("sheets")
        private val ALARM_CLOCKS_KEY = stringSetPreferencesKey("alarm_clocks")
    }

    ////////////
    // Fields //
    ////////////

    override val sheetsDTO: Flow<Set<String>> = dataStore.data
        .map { preferences ->
            preferences[SHEETS_KEY] ?: emptySet()
        }

    override val sheets: Flow<Set<StylusState>> = sheetsDTO
        .map { sheetsDTO ->
            sheetsDTO.map { it.toStylusState() }.toSet()
        }

    override val alarmClocksDTO: Flow<Set<String>> = dataStore.data
        .map { preferences ->
            preferences[ALARM_CLOCKS_KEY] ?: emptySet()
        }

    override val alarmClocks: Flow<Set<AlarmClock>> = alarmClocksDTO
        .map { alarmClocksDTO ->
            alarmClocksDTO.map { it.toAlarmClock() }.toSet()
        }
    
    ////////////
    // Sheets //
    ////////////

    override suspend fun get_sheets(): Set<StylusState> {
        return withContext(Dispatchers.IO) {
            this@UserPreferences.sheetsDTO.first().map {
                it.toStylusState()
            }.toSet()
        }
    }

    override suspend fun save_sheets(values: Set<StylusState>) {
        context.dataStore.edit { preferences ->
            preferences[SHEETS_KEY] = values
                .map { it.toDTO() }.toSet()
        }
    }

    override suspend fun add_sheet(sheet: StylusState) {
        withContext(Dispatchers.IO) {
            var existingSheets = sheetsDTO.first()
            val sheets = existingSheets.plus(sheet.toDTO())
            save_sheetsDTO(sheets)
        }
    }

    override suspend fun remove_sheet(sheet: StylusState) {
        withContext(Dispatchers.IO) {
            var existingSheets = sheetsDTO.first()
            val sheets = existingSheets.minus(sheet.toDTO())
            save_sheetsDTO(sheets)
        }
    }

    override suspend fun update_sheet(sheet: StylusState) {
        withContext(Dispatchers.IO) {
            var existingSheets = sheets.first()
            val toRemove = existingSheets
                .firstOrNull {it.name == sheet.name}
            if (toRemove == null)
                return@withContext
            
            val updated = existingSheets
                .minus(toRemove)
                .plus(sheet)
            
            save_sheets(updated)
        }
    }

    suspend fun save_sheetsDTO(values: Set<String>) {
        context.dataStore.edit { preferences ->
            preferences[SHEETS_KEY] = values
        }
    }

    //////////////////
    // alarm Clocks //
    //////////////////

    override suspend fun getAlarmClocks(): Set<AlarmClock> {
        return withContext(Dispatchers.IO) {
            this@UserPreferences.alarmClocksDTO.first().map {
                it.toAlarmClock()
            }.toSet()
        }
    }

    override suspend fun addAlarmClock(alarmClock: AlarmClock) {
        withContext(Dispatchers.IO) {
            var existingAlarmClocks = alarmClocksDTO.first()
            val alarmClocks = existingAlarmClocks.plus(alarmClock.toDTO())
            save_alarmClocksDTO(alarmClocks)
        }
    }

    override suspend fun save_alarmClocksDTO(alarmClocks: Set<String>) {
        context.dataStore.edit { preferences ->
            preferences[ALARM_CLOCKS_KEY] = alarmClocks
        }
    }

    override suspend fun removeAlarmClock(alarmClock: AlarmClock) {
        withContext(Dispatchers.IO) {
            var existingAlarmClocks = alarmClocksDTO.first()
            val sheets = existingAlarmClocks.minus(alarmClock.toDTO())
            save_alarmClocksDTO(sheets)
        }
    }

    override suspend fun replaceAlarmClock(alarmClock: AlarmClock) {
        withContext(Dispatchers.IO) {
            var existingAlarmClocks = alarmClocksDTO.first()
            val existingAlarmClock = existingAlarmClocks.find { it.toAlarmClock().Id == alarmClock.Id }
            if (existingAlarmClock != null) existingAlarmClocks = existingAlarmClocks.minus(existingAlarmClock)
            val alarmClocks = existingAlarmClocks.plus(alarmClock.toDTO())
            save_alarmClocksDTO(alarmClocks)
        }
    }

    override suspend fun replaceName(state: StylusState, string: String){
        withContext(Dispatchers.IO) {
            var existingAlarmClocks = alarmClocksDTO.first()
            
            var involvedAlarmClocks = existingAlarmClocks
                .map { acdto -> acdto.toAlarmClock() }
                .filter { ac -> ac.sheet == state.name }
            
            involvedAlarmClocks.forEach { ac ->
                val newAlarmClock = ac.copy(sheet = string)
                replaceAlarmClock(newAlarmClock)
            }
        } 
    }
}


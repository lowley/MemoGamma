package lorry.folder.items.memogamma.__data.userPreferences

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.toColorInt
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import lorry.folder.items.memogamma.bubble.BubbleViewModel
import lorry.folder.items.memogamma.bubble.DrawPoint
import lorry.folder.items.memogamma.bubble.DrawPointType
import lorry.folder.items.memogamma.bubble.StylusState
import lorry.folder.items.memogamma.bubble.StylusStatePath
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Singleton

val Context.dataStore by preferencesDataStore(name = "preferences_gamma")

@Singleton
open class UserPreferences @javax.inject.Inject constructor(private val context: Context) :
    IUserPreferences {

    private val dataStore = context.applicationContext.dataStore

    companion object {
        private val SHEETS_KEY = stringSetPreferencesKey("sheets")
    }

    //dto
    override val sheetsDTO: Flow<Set<String>> = dataStore.data
        .map { preferences ->
            preferences[SHEETS_KEY] ?: emptySet()
        }

    //stylusState
    override val sheets: Flow<Set<StylusState>> = sheetsDTO
        .map { sheetsDTO ->
            sheetsDTO.map { it.toStylusState() }.toSet()
        }

    //interface dto-metier
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
            existingSheets = existingSheets.plus(sheet.toDTO())
            save_sheetsDTO(existingSheets)
        }
    }

    override suspend fun update_sheet(sheet: StylusState) {
        withContext(Dispatchers.IO) {
            var existingSheets = sheets.first()
            val updated = existingSheets
                .map { if (it.name == sheet.name) sheet else it }
                .toSet()
            save_sheets(updated)
        }
    }

    suspend fun save_sheetsDTO(values: Set<String>) {
        context.dataStore.edit { preferences ->
            preferences[SHEETS_KEY] = values
        }
    }
}

fun StylusState.toDTO(): String {
    val json = JSONObject()
    json.put("name", name)

    val jsonItems = JSONArray()
    for (item in items) {
        val jsonItem = JSONObject()
        jsonItem.put("color", "#%06X".format(0xFFFFFF and item.color.toArgb()))
        jsonItem.put("width", item.style.width)

        val jsonPoints = JSONArray()
        for (point in item.pointList) {
            val jsonPoint = JSONObject()
            jsonPoint.put("x", point.x)
            jsonPoint.put("y", point.y)
            jsonPoint.put("type", point.type.name)
            jsonPoints.put(jsonPoint)
        }

        jsonItem.put("points", jsonPoints)
        jsonItems.put(jsonItem)
    }

    json.put("items", jsonItems)
    return json.toString()
}

fun String.toStylusState(): StylusState {
    val json = JSONObject(this)
    val name = json.getString("name")
    val jsonItems = json.getJSONArray("items")

    val items = mutableListOf<StylusStatePath>()

    for (i in 0 until jsonItems.length()) {
        val obj = jsonItems.getJSONObject(i)

        val color = Color(obj.getString("color").toColorInt())
        val stroke = Stroke(obj.getDouble("width").toFloat())

        val jsonPoints = obj.getJSONArray("points")
        val points = mutableListOf<DrawPoint>()
        for (j in 0 until jsonPoints.length()) {
            val p = jsonPoints.getJSONObject(j)
            val x = p.getDouble("x").toFloat()
            val y = p.getDouble("y").toFloat()
            val type = DrawPointType.valueOf(p.getString("type"))
            points.add(DrawPoint(x, y, type))
        }

        val path = BubbleViewModel.createPath(points)
        items.add(StylusStatePath(path, color, stroke, points))
    }

    return StylusState(name, items)
}
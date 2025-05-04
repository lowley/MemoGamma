package lorry.folder.items.memogamma.components.extensions

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.toColorInt
import lorry.folder.items.memogamma.bubble.BubbleViewModel
import lorry.folder.items.memogamma.components.dataClasses.AlarmClock
import lorry.folder.items.memogamma.components.dataClasses.DrawPoint
import lorry.folder.items.memogamma.components.dataClasses.DrawPointType
import lorry.folder.items.memogamma.components.dataClasses.StylusState
import lorry.folder.items.memogamma.components.dataClasses.StylusStatePath
import org.json.JSONArray
import org.json.JSONObject

/////////////////
// StylusState //
/////////////////

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

////////////////
// AlarmClock //
////////////////

fun AlarmClock.toDTO(): String {
    val json = JSONObject()
    
    json.put("realPackage", realPackage)
    json.put("friendlyPackage", friendlyPackage)
    json.put("sheet", sheet)
    
    return json.toString()
}

fun String.toAlarmClock(): AlarmClock {
    val json = JSONObject(this)
    val realPackage = json.getString("realPackage")
    val friendlyPackage = json.getString("friendlyPackage")
    val sheet = json.getString("sheet")

    return AlarmClock(realPackage, friendlyPackage, sheet)
}
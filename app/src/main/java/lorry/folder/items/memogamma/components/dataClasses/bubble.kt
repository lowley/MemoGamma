package lorry.folder.items.memogamma.components.dataClasses

import android.graphics.PointF
import android.view.MotionEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke

data class StylusState(
    var name: String,
    var items: MutableList<StylusStatePath> = mutableListOf<StylusStatePath>(),
) {
    fun copyDeep(): StylusState {
        val newItems = items.map { item ->
            StylusStatePath(
                path = Path().apply { addPath(item.path) }, // nouvelle instance
                color = Color(item.color.value),
                style = Stroke(item.style.width),
                pointList = item.pointList.map {
                    DrawPoint(
                        it.x,
                        it.y,
                        it.type
                    )
                }
            )
        }.toMutableList()

        return StylusState(this.name, newItems)
    }

    companion object {
        val DEFAULT = StylusState("Défaut", mutableListOf())
    }

//    fun isDefault() = this.items == DEFAULT.items

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StylusState

        return items == other.items && name == other.name
    }

    override fun hashCode(): Int {
        return items.hashCode() + name.hashCode()
    }
}

data class StylusStatePath(
    var path: Path,
    var color: Color,
    var style: Stroke,
    var pointList: List<DrawPoint>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StylusStatePath

        if (path != other.path) return false
        if (color != other.color) return false
        if (style != other.style) return false

        return true
    }

    override fun hashCode(): Int {
        var result = path.hashCode()
        result = 31 * result + color.hashCode()
        result = 31 * result + style.hashCode()
        return result
    }
}

class DrawPoint(
    x: Float,
    y: Float,
    val type: DrawPointType
) : PointF(x, y) {
    fun copy(x: Float = this.x, y: Float = this.y, type: DrawPointType = this.type): DrawPoint =
        DrawPoint(x, y, type)
}

enum class DrawPointType {
    START,
    LINE
}

data class TwoFingersScrollState(
    val startPoint: DrawPoint?,
    val endPoint: DrawPoint?,
) {
    var lastXvar: Float = 0f
    var lastYvar: Float = 0f
    
    val xvar: Float?
        get() = if (startPoint != null && endPoint != null) {
            lastXvar = endPoint.x - startPoint.x
            endPoint.x - startPoint.x
        }
        else lastXvar
    val yvar: Float?
        get() = if (startPoint != null && endPoint != null) {
            lastYvar = endPoint.y - startPoint.y
            endPoint.y - startPoint.y
        }
        else lastYvar
    
    val isDefined: Boolean = startPoint != null && endPoint != null

    companion object {
        var instance: TwoFingersScrollState? = null

        private fun ensureInstance(): TwoFingersScrollState {
            if (instance == null)
                instance = TwoFingersScrollState(null, null)
            return instance!!
        }

        private fun defineInstance(state: TwoFingersScrollState) {
            instance = state
        }

        fun setStartPoint(motionEvent: MotionEvent): TwoFingersScrollState {
            val startPoint = DrawPoint(motionEvent.x, motionEvent.y, DrawPointType.START)
            defineInstance(ensureInstance().copy(startPoint = startPoint))
            return instance!!
        }

        fun setEndPoint(motionEvent: MotionEvent): TwoFingersScrollState {
            val point = DrawPoint(motionEvent.x, motionEvent.y, DrawPointType.LINE)
            defineInstance(ensureInstance().copy(endPoint = point))
            return instance!!
        }

        
        
        val deltaX: Float?
            get() = ensureInstance().xvar ?: 0f

        val deltaY: Float?
            get() = ensureInstance().yvar ?: 0f

        fun reset() {
            instance = null
        }
    }
}

sealed class BubbleIntent {
    object ShowTotalDialog : BubbleIntent()
    object ShowBubbleDialog : BubbleIntent()
    object HideBubbleDialog : BubbleIntent()
    data class OpenDrawing(val name: String) : BubbleIntent()
}

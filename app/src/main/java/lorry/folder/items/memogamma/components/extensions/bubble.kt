package lorry.folder.items.memogamma.components.extensions

import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import lorry.folder.items.memogamma.components.dataClasses.DrawPoint
import lorry.folder.items.memogamma.components.dataClasses.DrawPointType
import lorry.folder.items.memogamma.components.dataClasses.StylusStatePath
import lorry.folder.items.memogamma.components.dataClasses.TwoFingersScrollState

fun Path.translate(dx: Float, dy: Float): Path {
    val matrix = Matrix()
    matrix.translate(dx, dy)
    this.transform(matrix)
    return this
}

fun MutableList<DrawPoint>.createPath(): Path {
    val path = Path()

    for (point in this) {
        if (point.type == DrawPointType.START) {
            path.moveTo(point.x, point.y)
        } else {
            path.lineTo(point.x, point.y)
        }
    }
    return path
}

fun MutableList<StylusStatePath>.translate(twoFingersScrollState: TwoFingersScrollState.Companion): MutableList<StylusStatePath> {
    return this.map { item ->
        val newPath = Path().apply {
            addPath(item.path)
            transform(Matrix().apply {
                translate(
                    twoFingersScrollState.deltaX ?: 0f,
                    twoFingersScrollState.deltaY ?: 0f
                )
            })
        }
        item.copy(path = newPath)
    }.toMutableList()
}
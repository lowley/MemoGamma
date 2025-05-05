package lorry.folder.items.memogamma.components.extensions

import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import lorry.folder.items.memogamma.components.dataClasses.DrawPoint
import lorry.folder.items.memogamma.components.dataClasses.DrawPointType

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
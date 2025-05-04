package lorry.folder.items.memogamma.components.extensions

import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path

fun Path.translate(dx: Float, dy: Float): Path {
    val matrix = Matrix()
    matrix.translate(dx, dy)
    this.transform(matrix)
    return this
}

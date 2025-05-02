package lorry.folder.items.memogamma.bubble

import android.graphics.PointF
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.github.only52607.compose.window.dragFloatingWindow
import lorry.folder.items.memogamma.R
import lorry.folder.items.memogamma.persistence.PersistencePopup
import lorry.folder.items.memogamma.undoRedo.StylusColorUndoRedo
import lorry.folder.items.memogamma.undoRedo.StylusStrokeUndoRedo
import lorry.folder.items.memogamma.undoRedo.UndoRedoManager

@Composable
fun BubbleContent(viewModel: BubbleViewModel) {
    //val floatingWindow = LocalFloatingWindow.current
    val visibilityState by viewModel.bubbleState.collectAsState(BubbleState.HIDDEN)
    val stylusColor by viewModel.stylusColor.collectAsState()
    val pointerCount by viewModel.pointerCount.collectAsState()
    val pointerName1 by viewModel.pointerName1.collectAsState()
    val pointerName2 by viewModel.pointerName2.collectAsState()
    val action by viewModel.pointerAction.collectAsState()
    val activePointer by viewModel.activePointer.collectAsState()
    val showPersistencePopup = viewModel.persistencePopupVisible.collectAsState(false)
    val recomposeTriggerPopup by viewModel.recomposePopupTrigger.collectAsState()

    Surface(
        modifier = Modifier
            //.align(Alignment.Center)
            .wrapContentSize(),
        shape = MaterialTheme.shapes.medium,
        color = Color(0xFFfefae0),
        tonalElevation = 0.dp,
        border = BorderStroke(1.dp, Color(0x55d4a373)),
    ) {
        Column(
            modifier = Modifier
                .padding(0.dp)
                .zIndex(1f)
        ) {
            Row(
                modifier = Modifier
                    .background(Color.Transparent)
            ) {
                val haptic = LocalHapticFeedback.current

                if (visibilityState != BubbleState.HIDDEN) {
                    FloatingActionButton(
                        modifier = Modifier
                            .alignByBaseline()
                            .dragFloatingWindow()
                            .zIndex(0f)
                            .size(40.dp),
                        onClick = {}
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = {
                                            BubbleManager.toggleBubbleTotal()
                                        },
                                        onLongPress = {
                                            BubbleManager.hide()
                                        }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                modifier = Modifier
                                    .background(Color.Transparent)
                                    .size(35.dp)
                                    .zIndex(0f),
                                painter = if (visibilityState == BubbleState.BUBBLE)
                                    painterResource(R.drawable.palette) else painterResource(R.drawable.group),
                                tint = Color.Unspecified,
                                contentDescription = "Ouvrir / Fermer"
                            )
                        }
                    }
                }

                if (visibilityState == BubbleState.TOTAL) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text(
                            modifier = Modifier
                                .width(300.dp)
                                .height(40.dp),
                            text = "Saisie de dessin",
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp
                        )
                    }
                }

                if (visibilityState == BubbleState.TOTAL) {
                    val arrowSize = 30.dp

                    val a by UndoRedoManager.changeNotifier.collectAsState()

                    Icon(
                        modifier = Modifier
                            .padding(end = 5.dp, top = 5.dp)
                            .zIndex(0f)
                            .clickable {
                                if (!UndoRedoManager.currentPositionIsBeforeFirst())
                                    UndoRedoManager.undo()
                            }
                            .size(arrowSize),
                        painter = painterResource(R.drawable.gauche),
                        tint = if (!UndoRedoManager.currentPositionIsBeforeFirst())
                            Color.Black else Color.Gray,
                        contentDescription = "undo$a"
                    )

                    Icon(
                        modifier = Modifier
                            .padding(end = 10.dp, top = 5.dp)
                            .zIndex(0f)
                            .clickable {
                                if (!UndoRedoManager.currentPositionIsAfterLast())
                                    UndoRedoManager.redo()
                            }
                            .size(arrowSize),
                        painter = painterResource(R.drawable.droite),
                        tint = if (UndoRedoManager.items.isNotEmpty() && !UndoRedoManager.currentPositionIsAfterLast())
                            Color.Black else Color.Gray,
                        contentDescription = "redo$a"
                    )

                    Icon(
                        modifier = Modifier
                            .padding(end = 10.dp, top = 5.dp)
                            .zIndex(0f)
                            .clickable {
                                viewModel.setPersistencePopupVisible(true)
                            }
                            .size(arrowSize),
                        painter = painterResource(R.drawable.disque_dur),
                        tint = Color.Unspecified,
                        contentDescription = "files"
                    )
                    if (showPersistencePopup.value) {
                        key(recomposeTriggerPopup) {
                            PersistencePopup(viewModel)
                        }
                    }
                }
            }

            if (visibilityState == BubbleState.TOTAL) {

                val stylusState by viewModel.currentStylusState.collectAsState()

                StylusVisualization(
                    modifier = Modifier
                        .padding(top = 10.dp, start = 5.dp)
                        .height(30.dp)
                        .fillMaxWidth(),
                    viewModel = viewModel,
                    stylusColor
//                                .background(Color.Black.copy(alpha = 0.2f))
                )

//                Row {
//                    Text(text = "nb:${pointerCount}", modifier = Modifier.padding(end = 5.dp))
//                    Text(text = "actPtr:${activePointer}", modifier = Modifier.padding(end = 5.dp))
//                    Text(text = "action:${action}", modifier = Modifier.padding(end = 5.dp))
//                    Text(text = "typ$pointerName1", modifier = Modifier.padding(end = 5.dp))
//                    Text(text = "typ$pointerName2")
//                }

                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color(0x88d4a373)
                )

                DrawArea(
                    modifier = Modifier
                        .background(Color.White)
                        .height(300.dp),
                    viewModel,
                    stylusState,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StylusVisualization(
    modifier: Modifier = Modifier,
    viewModel: BubbleViewModel,
    stylusColor: Color
) {
    Row(
        modifier = modifier
            .padding(bottom = 5.dp)
    ) {
        val arrowSize = 30.dp
        Icon(
            modifier = Modifier
                .padding(end = 5.dp)
                .zIndex(0f)
                .clickable {
                    if (stylusColor == Color(0xFFA82D2D))
                        return@clickable
                    UndoRedoManager.add(
                        StylusColorUndoRedo(
                            stylusColor, Color(0xFFA82D2D), viewModel
                        )
                    )
                    viewModel.setStylusColor(Color(0xFFA82D2D))
                }
                .size(arrowSize),
            painter = if (stylusColor == Color(0xFFA82D2D))
                painterResource(R.drawable.stylo_plume_trait)
            else painterResource(R.drawable.stylo_plume_seul),
            tint = Color(0xFFA82D2D),
            contentDescription = "rouge"
        )

        Icon(
            modifier = Modifier
                .padding(end = 5.dp)
                .zIndex(0f)
                .size(arrowSize)
                .clickable {
                    if (stylusColor == Color(0xFF429325))
                        return@clickable
                    UndoRedoManager.add(
                        StylusColorUndoRedo(
                            stylusColor, Color(0xFF429325), viewModel
                        )
                    )
                    viewModel.setStylusColor(Color(0xFF429325))
                },
            painter = if (stylusColor == Color(0xFF429325))
                painterResource(R.drawable.stylo_plume_trait)
            else painterResource(R.drawable.stylo_plume_seul),
            tint = Color(0xFF429325),
            contentDescription = "vert"
        )

        Icon(
            modifier = Modifier
                .padding(end = 5.dp)
                .zIndex(0f)
                .size(arrowSize)
                .clickable {
                    if (stylusColor == Color(0xFF5068C2))
                        return@clickable
                    UndoRedoManager.add(
                        StylusColorUndoRedo(
                            stylusColor, Color(0xFF5068C2), viewModel
                        )
                    )
                    viewModel.setStylusColor(Color(0xFF5068C2))
                },
            painter = if (stylusColor == Color(0xFF5068C2))
                painterResource(R.drawable.stylo_plume_trait)
            else painterResource(R.drawable.stylo_plume_seul),
            tint = Color(0xFF5068C2),
            contentDescription = "bleu"
        )

        Icon(
            modifier = Modifier
                .padding(end = 5.dp)
                .size(arrowSize)
                .zIndex(0f)
                .clickable {
                    if (stylusColor == Color(0xFF000000))
                        return@clickable
                    UndoRedoManager.add(
                        StylusColorUndoRedo(
                            stylusColor, Color(0xFF000000), viewModel
                        )
                    )
                    viewModel.setStylusColor(Color(0xFF000000))
                },
            painter = if (stylusColor == Color(0xFF000000))
                painterResource(R.drawable.stylo_plume_trait)
            else painterResource(R.drawable.stylo_plume_seul),
            tint = Color(0xFF000000),
            contentDescription = "noir"
        )

        Spacer(
            modifier = Modifier.weight(1f)
        )

        var stroke = viewModel.stylusStroke.collectAsState()

        Text(
            modifier = Modifier
                .padding(start = 5.dp, end = 5.dp)
                .align(Alignment.CenterVertically),
            text = String.format(locale = java.util.Locale.FRENCH, "%.2f", stroke.value.width)
        )

        var sliderStartValue by remember { mutableStateOf<Float?>(null) }

        Slider(
            modifier = Modifier
                .padding(end = 10.dp)
                .width(200.dp),
            value = stroke.value.width,
            valueRange = 0f..5f,
            onValueChange = {
                if (sliderStartValue == null) {
                    sliderStartValue =
                        stroke.value.width // Capture l'ancienne valeur au 1er changement
                }
                viewModel.setStylusStroke(Stroke(it))
            },
            onValueChangeFinished = {
                if (sliderStartValue != null) {
                    UndoRedoManager.add(
                        StylusStrokeUndoRedo(
                            Stroke(sliderStartValue!!),
                            Stroke(stroke.value.width),
                            viewModel
                        )
                    )
                    sliderStartValue = null // Remise à zéro pour le prochain glissement
                }
            },
            thumb = {
                // Le thumb personnalisé, ici une barre verticale de 10x30 dp
                Box(
                    modifier = Modifier
                        .size(width = stroke.value.width.dp, height = 30.dp)
                        .background(stylusColor, shape = RoundedCornerShape(2.dp))
                )
            },
            colors = SliderDefaults.colors(
                activeTrackColor = Color(0xFFccd5ae),     // partie gauche
                inactiveTrackColor = Color(0xFFe9edc9) // partie droite
            )
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DrawArea(
    modifier: Modifier = Modifier,
    viewModel: BubbleViewModel,
    stylusState: StylusState,
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
//            .border(1.dp, Color.Red)
            .clickable(enabled = true) {}
            .clipToBounds()
            .pointerInteropFilter {
                viewModel.processMotionEvent(it)
            }
    ) {
        stylusState.items.forEach { item ->
            drawPath(
                path = item.path,
                color = item.color,
                style = item.style
            )
        }
    }
}

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
                pointList = item.pointList.map { DrawPoint(
                    it.x,
                    it.y,
                    it.type
                ) }
            )
        }.toMutableList()

        return StylusState(this.name, newItems)
    }

    companion object {
        val DEFAULT = StylusState("Défaut", mutableListOf())
    }

    fun isDefault() = this.items == DEFAULT.items

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
    val xvar: Float?
        get() = if (startPoint != null && endPoint != null)
            endPoint.x - startPoint.x
        else null
    val yvar: Float?
        get() = if (startPoint != null && endPoint != null)
            endPoint.y - startPoint.y
        else null
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

        fun setStartPoint(startPoint: DrawPoint): TwoFingersScrollState {
            defineInstance(ensureInstance().copy(startPoint = startPoint))
            return instance!!
        }

        fun setEndPoint(endPoint: DrawPoint): TwoFingersScrollState {
            defineInstance(ensureInstance().copy(endPoint = endPoint))
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




package lorry.folder.items.memogamma.bubble

import android.graphics.PointF
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.github.only52607.compose.window.dragFloatingWindow
import lorry.folder.items.memogamma.R

@Composable
fun BubbleContent(viewModel: BubbleViewModel) {
    //val floatingWindow = LocalFloatingWindow.current
    val visibilityState by viewModel.bubbleState.collectAsState(BubbleState.HIDDEN)
    val stylusColor by viewModel.stylusColor.collectAsState()

    Surface(
        modifier = Modifier
            //.align(Alignment.Center)
            .wrapContentSize()
            .background(Color.Transparent),
        shape = MaterialTheme.shapes.medium,
        color = Color.Transparent,
        tonalElevation = 0.dp,
        border = BorderStroke(1.dp, Color.DarkGray),
    ) {
        Column(
            modifier = Modifier
                .padding(0.dp)
                .zIndex(1f)
        ) {
            Row(
                modifier = Modifier
            ) {
                if (visibilityState != BubbleState.HIDDEN) {
                    FloatingActionButton(
                        modifier = Modifier
                            .alignByBaseline()
                            .dragFloatingWindow()
                            .zIndex(0f)
                            .size(40.dp), // au-dessus
                        onClick = {
                            BubbleManager.toggleBubbleTotal()
                        }
                    ) {
                        Icon(
                            modifier = Modifier
                                .zIndex(0f),
                            imageVector = if (visibilityState == BubbleState.BUBBLE)
                                Icons.Default.ThumbUp else Icons.Default.ThumbDown,
                            contentDescription = "Ouvrir / Fermer"
                        )
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
            }

            if (visibilityState == BubbleState.TOTAL) {

                val stylusState by viewModel.stylusState.collectAsState()

                StylusVisualization(
                    modifier = Modifier
                        .padding(top =  10.dp, start = 5.dp)
                        .height(30.dp)
                        .fillMaxWidth(),
                    viewModel = viewModel
//                                .background(Color.Black.copy(alpha = 0.2f))
                )
                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color.Black
                )
                DrawArea(
                    modifier = Modifier
                        .background(Color.White)
                        .height(300.dp),
                    viewModel,
                    stylusState,
                    stylusColor
                )
            }
        }
    }
}

@Composable
fun StylusVisualization(
    modifier: Modifier = Modifier,
    viewModel: BubbleViewModel
) {
    Row(
        modifier = modifier
    ) {
        Icon(
            modifier = Modifier
                .padding(end = 5.dp)
                .zIndex(0f)
                .clickable {
                    viewModel.setStylusColor(Color(0xFFA82D2D))
                }
                .size(20.dp),
            painter = painterResource(R.drawable.plume),
            tint = Color(0xFFA82D2D),
            contentDescription = "rouge"
        )

        Icon(
            modifier = Modifier
                .padding(end = 5.dp)
                .zIndex(0f)
                .size(20.dp)
                .clickable {
                    viewModel.setStylusColor(Color(0xFF429325))
                },
            painter = painterResource(R.drawable.plume),
            tint = Color(0xFF429325),
            contentDescription = "vert"
        )

        Icon(
            modifier = Modifier
                .padding(end = 5.dp)
                .zIndex(0f)
                .size(20.dp)
                .clickable {
                    viewModel.setStylusColor(Color(0xFF5068C2))
                },
            painter = painterResource(R.drawable.plume),
            tint = Color(0xFF5068C2),
            contentDescription = "bleu"
        )

        Icon(
            modifier = Modifier
                .padding(end = 5.dp)
                .size(20.dp)
                .zIndex(0f)
                .clickable {
                    viewModel.setStylusColor(Color(0xFF000000))
                },
            painter = painterResource(R.drawable.plume),
            tint = Color(0xFF000000),
            contentDescription = "noir"
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DrawArea(
    modifier: Modifier = Modifier,
    viewModel: BubbleViewModel,
    stylusState: StylusState,
    stylusColor: Color
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
        with(stylusState) {
            drawPath(
                path = this.path,
                color = stylusColor,
                style = Stroke(width = 3f)
            )
        }
    }
}

data class StylusState(
    var pressure: Float = 0F,
    var orientation: Float = 0F,
    var tilt: Float = 0F,
    var path: Path = Path(),
)

class DrawPoint(x: Float, y: Float, val type: DrawPointType) : PointF(x, y)

enum class DrawPointType {
    START,
    LINE
}




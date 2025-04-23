package lorry.folder.items.memogamma.bubble

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.github.only52607.compose.window.dragFloatingWindow

@Composable
fun BubbleContent(viewModel: BubbleViewModel) {
    //val floatingWindow = LocalFloatingWindow.current
    val visibilityState by viewModel.bubbleState.collectAsState(
        BubbleState.HIDDEN
    )

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
        Box(
            modifier = Modifier
                .padding(0.dp)
                .zIndex(1f)
        ) {
            if (visibilityState != BubbleState.HIDDEN) {
                FloatingActionButton(
                    modifier = Modifier
                        .align(Alignment.TopStart)
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

                if (visibilityState == BubbleState.TOTAL) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .width(300.dp)
                            .height(40.dp),
                        text = "Saisie de dessin",
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp
                    )

                    var stylusState: StylusState by remember {  mutableStateOf(StylusState())}

                    Column {
                        StylusVisualization(
                            modifier = Modifier
                                .height(100.dp).fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.2f))
                        )
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = Color.Black
                        )
                        DrawArea(
                            modifier = Modifier
                                .background(Color.White)
                                .height(300.dp)
                        )
                    }


                }
            }
        }
    }
}

@Composable
fun StylusVisualization(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
    ) {

    }
}

@Composable

fun DrawArea(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .clipToBounds()

    ) {

    }
}

data class StylusState(
    var pressure: Float = 0F,
    var orientation: Float = 0F,
    var tilt: Float = 0F,
    var path: Path = Path(),
)






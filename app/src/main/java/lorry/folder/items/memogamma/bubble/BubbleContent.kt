package lorry.folder.items.memogamma.bubble

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
        Column(
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.2f))
                .padding(0.dp)
                .zIndex(1f),
            verticalArrangement = Arrangement.Center
        ) {
            if (visibilityState != BubbleState.HIDDEN) {
                FloatingActionButton(
                    modifier = Modifier
                        .align(Alignment.Start)
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
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        text = "inside the bubble"
                    )

                }
            }
        }
    }
}






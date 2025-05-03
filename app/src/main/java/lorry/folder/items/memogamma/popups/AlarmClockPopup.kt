package lorry.folder.items.memogamma.popups

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import lorry.folder.items.memogamma.R
import lorry.folder.items.memogamma.bubble.BubbleViewModel

@Composable
fun AlarmClockPopup(
    viewModel: BubbleViewModel
) {
    Popup(
        alignment = Alignment.TopCenter,
        properties = PopupProperties(focusable = true)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 8.dp,
            color = Color(0xFFfefae0),
            border = BorderStroke(1.dp, Color(0xFFd4a373))
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .width(400.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AlarmClockBody(
                    modifier = Modifier,
                    viewModel = viewModel,
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 0.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        viewModel.setAlarmClockPopupVisible(false)
                        viewModel.changeRecomposeAlarmClockPopupTrigger()
                    }) {
                        Text("Fermer")
                    }
                }
            }
        }
    }
}

@Composable
fun AlarmClockBody(
    modifier: Modifier = Modifier,
    viewModel: BubbleViewModel
) {
    val arrowSize = 30.dp
    val eyeSize = 35.dp

    val entetes = listOf("Application", "Feuille")
    
    Column(
        modifier = Modifier,
    ) {
        Row {
            entetes.forEach { titre ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                        .height(24.dp)
                ) {
                    Text(
                        text = titre,
                        modifier = Modifier
                            .align(Alignment.Center),
                        fontWeight = FontWeight.Bold,
                    )

                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .padding(horizontal = 5.dp)
                            .background(Color.LightGray)
                            .align(Alignment.BottomCenter)
                    )
                }
            }

            Icon(
                modifier = Modifier
                    .padding(start = 5.dp, end = 5.dp)
                    .zIndex(0f)
                    .size(20.dp)
                    .clickable {

                    },
                painter = painterResource(R.drawable.poubelle),
                tint = Color.Transparent,
                contentDescription = "supprimer"
            )
        }

        val targetApplication = viewModel.userPreferences.reactivePackage.collectAsState("")
        val targetSheet = viewModel.userPreferences.drawingToLoad.collectAsState("")
        val datas = listOf(targetApplication, targetSheet)

        Row(
            modifier = Modifier
//                .padding(0.dp)
        ) {
            datas.forEach { cellule ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp, horizontal = 5.dp)
                        .height(24.dp)
                ) {
                    Text(
                        text = cellule.value,
                        modifier = Modifier
                            .align(Alignment.Center),
                        fontWeight = FontWeight.Normal
                    )

                    Icon(
                        modifier = Modifier
                            .zIndex(0f)
                            .align(Alignment.CenterEnd)
                            .size(eyeSize)
                            .clickable {

                            },
                        painter = painterResource(R.drawable.stylo_plume_seul),
                        tint = Color(0xFFccd5ae),
                        contentDescription = "editer"
                    )
                }
            }

            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .height(24.dp)
            ) {
                Icon(
                    modifier = Modifier
                        .padding(start = 5.dp, end = 5.dp)
                        .align(Alignment.Center)
                        .zIndex(0f)
                        .size(20.dp)
                        .padding(bottom = 2.dp)
                        .clickable {

                        },
                    painter = painterResource(R.drawable.poubelle),
                    tint = Color(0xFFccd5ae),
                    contentDescription = "supprimer"
                )
            }
        }

        // Lignes de données
//        lignes.forEach { ligne ->
//            Row {
//                ligne.forEach { cellule ->
//                    Text(
//                        text = cellule,
//                        modifier = Modifier
//                            .weight(1f)
//                            .padding(8.dp)
//                    )
//                }
//            }
//        }
    }
    
}

package lorry.folder.items.memogamma.popups

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import lorry.folder.items.memogamma.R
import lorry.folder.items.memogamma.bubble.BubbleViewModel
import lorry.folder.items.memogamma.components.dataClasses.StylusState

@Composable
fun PersistencePopup(
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
                Body(
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
                        viewModel.setPersistencePopupVisible(false)
                        viewModel.changeRecomposePersistencePopupTrigger()
                    }) {
                        Text("Fermer")
                    }
                }

            }
        }
    }
}

@Composable
fun Body(
    modifier: Modifier = Modifier,
    viewModel: BubbleViewModel
) {
    val arrowSize = 30.dp
    val eyeSize = 35.dp
    var newName by remember { mutableStateOf("") }
    val initialStylusState by viewModel.initialStylusState.collectAsState()
    val currentStylusState by viewModel.currentStylusState.collectAsState()
    val sheets by viewModel.drawings.collectAsState(emptySet())

    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val context = LocalContext.current
        
        Row(
            modifier = Modifier
        ) {
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 10.dp, end = 5.dp)
                    .zIndex(0f)
                    .clickable {
                        viewModel.setInitialStylusState(StylusState.DEFAULT)
                        viewModel.setCurrentStylusState(
                            StylusState(
                                name = "",
                                items = mutableListOf(),
                            )
                        )
                        viewModel.setPersistencePopupVisible(false)
                        viewModel.changeRecomposePersistencePopupTrigger()
                    }
                    .size(arrowSize),
                painter = painterResource(R.drawable.ampoule),
                tint = Color.Unspecified,
                contentDescription = "nouveau"
            )

            TextField(
                modifier = Modifier
                    .weight(1f),
                value = newName,
                onValueChange = { newText: String ->
                    newName = newText
                },
                enabled = initialStylusState != currentStylusState,
                placeholder = {
                    Text(text = "enregistrer sous...")
                }
            )

            val canSaveAsNewFile = initialStylusState != currentStylusState
                    && newName.isNotBlank()
                    && !sheets.any { it.name == newName }

            val canSaveAsExistingFile = initialStylusState != currentStylusState

            Icon(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 5.dp, end = 10.dp)
                    .zIndex(0f)
                    .size(arrowSize)
                    .then(if (canSaveAsNewFile) Modifier.clickable {
                        viewModel.saveCurrentStateAs(currentStylusState, newName)
                        viewModel.changeRecomposePersistencePopupTrigger()
                        Toast.makeText(
                            context,
                            "Dessin ${newName} créé",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else Modifier)
                    .alpha(if (canSaveAsNewFile) 1f else 0.3f),
                painter = painterResource(R.drawable.disquette),
                tint = if (initialStylusState != currentStylusState) Color.Unspecified else Color.Gray,
                contentDescription = "enregistrer"
            )
        }

        val listState = rememberLazyListState()

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sheets.size) { index ->
                val drawing = sheets.elementAt(index)

                val canUpdate =
                    initialStylusState != currentStylusState
                            && initialStylusState.name == drawing.name
                            && currentStylusState.name == drawing.name

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp)
                        .height(50.dp)
                        .background(
                            color = Color(0xFFfefae0),
                            shape = RoundedCornerShape(5.dp)
                        )
                        .border(
                            width = 2.dp,
                            color = Color(0xFFfaedcd),
                            shape = RoundedCornerShape(5.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(24.dp)
                            .align(Alignment.CenterVertically)
                            .clickable {
                                viewModel.setState(drawing)
                                viewModel.setPersistencePopupVisible(false)
                                viewModel.changeRecomposePersistencePopupTrigger()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            modifier = Modifier,
                            text = drawing.name,
                        )
                    }

                    Row(
                        modifier = Modifier
                            .width(intrinsicSize = IntrinsicSize.Min)
                            .align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(start = 5.dp, end = 5.dp)
                                .zIndex(0f)
                                .size(eyeSize)
                                .then(if (canUpdate) modifier.clickable {
                                    val updated = if (newName.isNotEmpty()) currentStylusState.copy(name = newName)
                                    else currentStylusState
                                    viewModel.saveCurrentStateAs(updated, newName, replace = true)
                                    //toast
                                    viewModel.setInitialStylusState(updated)
                                    viewModel.setCurrentStylusState(updated)
                                    viewModel.setPersistencePopupVisible(false)
                                    viewModel.changeRecomposePersistencePopupTrigger()
                                    Toast.makeText(
                                        context,
                                        "Dessin $newName enregistré",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else Modifier)
                                .alpha(if (canUpdate) 1f else 0.3f),
                            painter = painterResource(R.drawable.disquette),
                            tint = Color(0xFFccd5ae),
                            contentDescription = "enregistrer"
                        )


                        Icon(
                            modifier = Modifier
                                .padding(start = 5.dp, end = 5.dp)
                                .zIndex(0f)
                                .size(eyeSize)
                                .clickable {
                                    val name = drawing.name
                                    viewModel.deleteDrawing(drawing)
                                    Toast.makeText(
                                        context,
                                        "Dessin ${name} supprimé",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    //viewModel.changeRecomposePopupTrigger()
                                },
                            painter = painterResource(R.drawable.poubelle),
                            tint = Color(0xFFccd5ae),
                            contentDescription = "supprimer"
                        )
                    }
                }
            }
        }
    }
}

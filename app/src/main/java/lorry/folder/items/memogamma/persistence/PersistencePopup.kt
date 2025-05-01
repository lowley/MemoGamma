package lorry.folder.items.memogamma.persistence

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import lorry.folder.items.memogamma.R
import lorry.folder.items.memogamma.bubble.BubbleViewModel
import lorry.folder.items.memogamma.bubble.StylusState

@Composable
fun PersistencePopup(
    showPopup: MutableState<Boolean>,
    viewModel: BubbleViewModel
) {
    Popup(
        alignment = Alignment.TopCenter,
        properties = PopupProperties(focusable = true)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 8.dp,
            color = Color.LightGray,
            border = BorderStroke(1.dp, Color.DarkGray)
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
                    showPopup = showPopup
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 0.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        showPopup.value = false
                    }) {
                        Text("Annuler")
                    }
                    TextButton(onClick = {
                        showPopup.value = false
                    }) {
                        Text("Valider")
                    }
                }

            }
        }
    }
}

@Composable
fun Body(
    modifier: Modifier = Modifier,
    viewModel: BubbleViewModel,
    showPopup: MutableState<Boolean>
){
    val arrowSize = 30.dp
    var newName by remember { mutableStateOf("") }
    val initialStylusState by viewModel.initialStylusState.collectAsState()
    val currentStylusState by viewModel.currentStylusState.collectAsState()
    
    
    Column(
        modifier = Modifier, 
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
     
        Row(
            modifier = Modifier
        ){
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 10.dp, end = 5.dp)
                    .zIndex(0f)
                    .clickable {
                        viewModel.setInitialStylusState(StylusState.DEFAULT)
                        viewModel.setCurrentStylusState(StylusState(
                            name = "",
                            items = mutableListOf(),
                        ))
                        showPopup.value = false
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
                onValueChange = {newText:String -> 
                    newName = newText
                },
                enabled = initialStylusState != currentStylusState,
                placeholder = {
                    Text(text = "enregistrer sous..." )
                }
            )
            
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 5.dp, end = 10.dp)
                    .zIndex(0f)
                    .size(arrowSize)
                    .then(
                        if (initialStylusState != currentStylusState
                            && newName.isNotEmpty()
                        ) 
                            Modifier.clickable {
                            viewModel.saveCurrentStateAs(newName)
                        } else Modifier
                    ),
                painter = painterResource(R.drawable.disquette),
                tint = if (initialStylusState != currentStylusState) Color.Unspecified else Color.Gray,
                contentDescription = "enregistrer"
            )
        }

        val state by viewModel.drawings.collectAsState(emptySet())
        val listState = rememberLazyListState()
        
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.size) { index ->
                val drawing = state.elementAt(index)
                Text(
                    text = drawing.name,
                    modifier = Modifier
                )
            }
        }
    }
}
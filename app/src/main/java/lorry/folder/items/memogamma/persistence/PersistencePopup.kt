package lorry.folder.items.memogamma.persistence

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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

@Composable
fun PersistencePopup(
    showPopup: MutableState<Boolean>,
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
                    modifier = Modifier
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

){
    val arrowSize = 30.dp
    var newName by remember { mutableStateOf("") }
    
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
                placeholder = {
                    Text(text = "enregistrer sous..." )
                }
            )
            
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 5.dp, end = 10.dp)
                    .zIndex(0f)
                    .clickable {

                    }
                    .size(arrowSize),
                painter = painterResource(R.drawable.disquette),
                tint = Color.Unspecified,
                contentDescription = "enregistrer"
            )
            
            
            
            
        }
        
        
    }
}
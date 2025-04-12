package com.kristinakoneva.twistale.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kristinakoneva.twistale.ui.theme.spacing_1
import com.kristinakoneva.twistale.ui.theme.spacing_2
import com.kristinakoneva.twistale.ui.theme.spacing_3
import com.kristinakoneva.twistale.ui.theme.spacing_4

@Composable
fun AlertDialog(
    title: String,
    description: String,
    onConfirmClick: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                )
                .padding(spacing_3),
        ) {
            Row {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                    overflow = TextOverflow.Ellipsis,
                )
                IconButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.offset(x = spacing_2, y = -spacing_2),
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                    )
                }
            }
            Spacer(Modifier.height(spacing_2))
            Text(
                text = description,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
            )
            Spacer(Modifier.height(spacing_4))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                OutlinedButton(
                    onClick = onDismissRequest,
                ) {
                    Text("Cancel")
                }
                Spacer(Modifier.width(spacing_1))
                Button(
                    onClick = onConfirmClick,
                ) {
                    Text("Confirm")
                }
            }
        }
    }
}

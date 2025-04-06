package com.kristinakoneva.twistale.ui.screens.game.story.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.kristinakoneva.twistale.domain.game.models.RoundType
import com.kristinakoneva.twistale.domain.game.models.StoryPart
import com.kristinakoneva.twistale.ui.theme.spacing_1
import com.kristinakoneva.twistale.ui.theme.spacing_2

@Composable
fun StoryPartItem(
    item: StoryPart,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .width(300.dp)
            .padding(horizontal = spacing_2)
            .clip(shape = MaterialTheme.shapes.small)
            .background(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primaryContainer,
            )
            .border(
                width = 2.dp,
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primary,
            )
            .padding(spacing_2),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val actionText = when (item.roundType) {
            RoundType.WRITING -> "wrote ✍️:"
            RoundType.DRAWING -> "drew 🎨:"
        }
        Row(
            modifier = Modifier.align(Alignment.Start),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = item.player.name,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .clip(shape = MaterialTheme.shapes.small)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.small,
                    )
                    .padding(spacing_1),
                color = Color.White,
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 18.sp),
            )
            Spacer(modifier = Modifier.width(spacing_1))
            Text(
                text = actionText,
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 18.sp),
            )
        }
        Spacer(modifier = modifier.height(spacing_2))
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (item.roundType) {
                RoundType.WRITING -> Text(
                    text = "\"${item.input}\"",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 24.sp,
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )

                RoundType.DRAWING -> AsyncImage(
                    model = item.input,
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                )
            }
        }
    }
}

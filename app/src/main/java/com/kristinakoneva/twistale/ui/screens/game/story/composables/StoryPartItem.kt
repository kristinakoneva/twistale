package com.kristinakoneva.twistale.ui.screens.game.story.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.kristinakoneva.twistale.domain.game.models.RoundType
import com.kristinakoneva.twistale.domain.game.models.StoryPart

@Composable
fun StoryPartItem(
    item: StoryPart,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = item.player.name, textAlign = TextAlign.Center)
        Spacer(modifier = modifier.height(8.dp))
        when (item.roundType) {
            RoundType.WRITING -> Text(text = item.input, textAlign = TextAlign.Center)
            RoundType.DRAWING -> AsyncImage(
                model = item.input,
                contentDescription = null,
            )
        }
    }
}

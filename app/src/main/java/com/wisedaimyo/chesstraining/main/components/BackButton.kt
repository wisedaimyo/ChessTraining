package com.wisedaimyo.chesstraining.main.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wisedaimyo.chesstraining.main.ui.theme.dimens

@Composable
fun BackButton(onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(5.dp)
            .clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "",
            modifier = Modifier
                .size(MaterialTheme.dimens.medium1)
        )
        Text("SPÄŤ",
            fontWeight = FontWeight.ExtraBold,
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ColumnScope.RenderAccordionSection(modifier: Modifier = Modifier) {
    val isExpanded: Boolean = false

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Red, shape = RoundedCornerShape(8.dp))
            .padding(20.dp)
    ) {
        Column {
            Row {
                Text(
                    "Title Header",
                    color = Color.Black
                )

                Spacer(
                    modifier = Modifier
                        .weight(1f)
                )

                Box(
                    modifier = Modifier
                        .background(Color.Blue)
                        .size(20.dp, 20.dp)
                )
            }

            if (isExpanded) {
                Text(
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                    color = Color.Black
                )
            }
        }
    }
}

package ru.sponsr.downloader

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class Post(
    val id: Int,
    val title: String,
    val onDownloadClick: () -> Unit = {}
)

@Composable
fun PostList() {
    val posts = listOf(
        Post(1, "Великая Китайская стена: История строительства и значение"),
        Post(2, "Древний Египет: Тайны пирамид и фараонов"),
        Post(3, "Римская империя: Взлет и падение вечного города"),
        Post(4, "Эпоха Великих географических открытий"),
        Post(5, "Французская революция: Причины и последствия"),
        Post(6, "Вторая мировая война: Ключевые события и итоги"),
        Post(7, "Древняя Греция: Колыбель западной цивилизации"),
        Post(8, "Средневековые рыцари и кодекс чести"),
        Post(9, "Эпоха Возрождения: Расцвет искусства и науки"),
        Post(10, "Холодная война: Противостояние сверхдержав")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(posts) { post ->
            PostItem(post = post)
        }
    }
}

@Composable
private fun PostItem(post: Post) {
    Card(
        elevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = post.title,
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Medium
            )
            
            Button(
                onClick = post.onDownloadClick,
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Скачать",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Скачать")
            }
        }
    }
}
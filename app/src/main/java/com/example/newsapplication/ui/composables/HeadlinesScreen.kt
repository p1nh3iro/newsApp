package com.example.newsapplication.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.newsapplication.BuildConfig
import com.example.newsapplication.model.Article
import com.example.newsapplication.viewmodel.NewsUiState
import com.example.newsapplication.viewmodel.NewsViewModel

@Composable
fun HeadlinesScreen(
    viewModel: NewsViewModel = viewModel(),
    onSelectArticle: (Article) -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value

    when (uiState) {
        is NewsUiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is NewsUiState.Success -> {
            // Story 1 -> Point 1
            val articles = (uiState as NewsUiState.Success).articles as List<Article>
            Column {
                // TODO STORY 4
                Text(
                    text = BuildConfig.FLAVOR,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                )
                // Story 1 -> Point 2 & 5
                LazyColumn {
                    items(articles) { article ->
                        ArticleRow(article = article, onClick = { onSelectArticle(article) })
                    }
                }
            }

        }
        is NewsUiState.Error -> {
            Text(
                text = "Failed to load news: ${(uiState as NewsUiState.Error).message}",
                color = Color.Red,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun ArticleRow(article: Article, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(),
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            article.urlToImage.let { imageUrl ->
                // Story 1 -> Point 6
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            // Story 1 -> Point 3
            Text(
                text = article.title,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = article.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun DetailScreen(article: Article) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(article.title, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        article.urlToImage.let { imageUrl ->
            AsyncImage(model = imageUrl, contentDescription = null)
            Spacer(Modifier.height(8.dp))
        }
        Text(
            text = cleanContent(article.description),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = cleanContent(article.content),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

fun cleanContent(raw: String?): String {
    return raw?.substringBefore("[+")?.trim() ?: "No content available"
}
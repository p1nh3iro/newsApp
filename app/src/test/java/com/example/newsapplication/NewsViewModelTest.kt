package com.example.newsapplication

import com.example.newsapp.network.RetrofitInstance
import com.example.newsapplication.model.Article
import com.example.newsapplication.model.Source
import com.example.newsapplication.repository.NewsRepository
import com.example.newsapplication.viewmodel.NewsUiState
import com.example.newsapplication.viewmodel.NewsViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

// test/NewsViewModelTest.kt
@OptIn(ExperimentalCoroutinesApi::class)
class NewsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: NewsViewModel

    // Fake repository that returns a known list
    private val fakeArticles = listOf(
        element = Article(
            author = "Test",
            content = "Desc",
            description = "",
            publishedAt = "",
            source = Source("", ""),
            title = "",
            url = "",
            urlToImage = ""
        )
    )

    private val fakeRepo = object : NewsRepository(api = RetrofitInstance.api) {
        override suspend fun getTopHeadlines(): List<Article> {
            return fakeArticles
        }
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = NewsViewModel(fakeRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState is Success when repository returns data`() = runTest {
        // Advance coroutine until all tasks complete
        testDispatcher.scheduler.advanceUntilIdle()

        // The ViewModel should now have loaded data
        val state = viewModel.uiState.value
        assertTrue(state is NewsUiState.Success)
        assertEquals(fakeArticles, (state as NewsUiState.Success).articles)
    }
}

package com.example.newsapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapplication.repository.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewsViewModel(private val repository: NewsRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<NewsUiState>(NewsUiState.Loading)
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val list = repository.getTopHeadlines()
                // Story 1 -> Point 4 - Sort the list by date
                _uiState.value = NewsUiState.Success(list.sortedBy { it.publishedAt })
            } catch (e: Exception) {
                _uiState.value = NewsUiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }
}

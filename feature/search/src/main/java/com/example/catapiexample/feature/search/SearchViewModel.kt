package com.example.catapiexample.feature.search

import Breed
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linker.core.domain.GetBreedsSearchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@HiltViewModel
class SearchViewModel(
    private val searchBreedsUseCase: GetBreedsSearchUseCase
): ViewModel() {


    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Breed>>(emptyList())
    val searchResults: StateFlow<List<Breed>> = _searchResults.asStateFlow()

    private val _searchError = MutableStateFlow<String?>(null)
    val searchError: StateFlow<String?> = _searchError.asStateFlow()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private suspend fun observeSearchResults() {
        _searchQuery
            .debounce(300) // تأخیر 300 میلی‌ثانیه
            .filter { it.isNotBlank() } // فقط کوئری‌های غیرخالی
            .flatMapLatest { query ->
                searchBreedsUseCase(query)
            }.collect { results ->
                _searchResults.value = results
                _searchError.value = null // پاک کردن ارور در صورت موفقیت
            }
    }

    init {
        viewModelScope.launch {
            observeSearchResults()
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            _searchError.value = null
        } else {
            viewModelScope.launch {
                try {
                    searchBreedsUseCase(query).collect { results ->
                        _searchResults.value = results
                        _searchError.value = null
                    }
                } catch (e: Exception) {
                    _searchError.value = "Failed to search breeds: ${e.message}"
                    _searchResults.value = emptyList()
                }
            }
        }
    }

    fun clearSearchError() {
        _searchError.value = null
    }


}
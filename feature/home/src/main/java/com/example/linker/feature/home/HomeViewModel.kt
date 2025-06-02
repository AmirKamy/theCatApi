package com.example.linker.feature.home

import Breed
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.linker.core.model.Resource
import com.linker.core.domain.GetBreedsSearchUseCase
import com.linker.core.domain.GetBreedsUseCase
import com.linker.core.domain.GetFavoriteBreedsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getBreedsUseCase: GetBreedsUseCase,
    private val getFavoriteBreedsUseCase: GetFavoriteBreedsUseCase,
    private val searchBreedsUseCase: GetBreedsSearchUseCase
) : ViewModel() {

    val breeds: Flow<PagingData<Breed>> = getBreedsUseCase.invoke()
        .cachedIn(viewModelScope)

    val breedForDetail = mutableStateOf<Breed?>(null)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<Resource<List<Breed>>>(Resource.Success(emptyList()))
    val searchResults: StateFlow<Resource<List<Breed>>> = _searchResults.asStateFlow()

    init {
        viewModelScope.launch {
            observeSearchResults()
        }
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private suspend fun observeSearchResults() {
        _searchQuery
            .debounce(300)
            .filter { it.isNotBlank() }
            .flatMapLatest { query ->
                searchBreedsUseCase(query).map { result ->
                    when (result) {
                        is Resource.Success -> Resource.Success(result.data)
                        is Resource.Error -> Resource.Error(getUserFriendlyErrorMessage(result.message))
                        is Resource.Loading -> Resource.Loading
                    }
                }
            }.collect { resource ->
                _searchResults.value = resource
            }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _searchResults.value = Resource.Success(emptyList())
        } else {
            viewModelScope.launch {
                _searchResults.value = Resource.Loading
                try {
                    searchBreedsUseCase(query).collect { resource ->
                        _searchResults.value = when (resource) {
                            is Resource.Success -> Resource.Success(resource.data)
                            is Resource.Error -> Resource.Error(getUserFriendlyErrorMessage(resource.message))
                            is Resource.Loading -> Resource.Loading
                        }
                    }
                } catch (e: Exception) {
                    _searchResults.value = Resource.Error(getUserFriendlyErrorMessage(e.message ?: "Unknown error"))
                }
            }
        }
    }

    fun retrySearch() {
        if (_searchQuery.value.isNotBlank()) {
            setSearchQuery(_searchQuery.value)
        }
    }

    fun toggleFavorite(breedId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            try {
                getFavoriteBreedsUseCase.invoke(breedId, isFavorite)
            } catch (e: Exception) {
                Log.e("error", e.toString())
            }
        }
    }

    fun setSelectedBreed(breed: Breed) {
        breedForDetail.value = breed
    }

    private fun getUserFriendlyErrorMessage(error: String): String {
        return when {
            error.contains("HTTP 4", ignoreCase = true) -> "Server error. Please try again later."
            error.contains("network", ignoreCase = true) -> "No internet connection. Showing cached data."
            else -> "An error occurred: $error. Please try again."
        }
    }
}
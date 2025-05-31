package com.example.linker.feature.home

import Breed
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.linker.core.domain.GetBreedsUseCase
import com.linker.core.domain.GetFavoriteBreedsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getBreedsUseCase: GetBreedsUseCase,
    private val getFavoriteBreedsUseCase: GetFavoriteBreedsUseCase,
) : ViewModel() {

    val breeds: Flow<PagingData<Breed>> = getBreedsUseCase.invoke()
        .cachedIn(viewModelScope)


    fun toggleFavorite(breedId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            getFavoriteBreedsUseCase.invoke(breedId, isFavorite)
        }
    }



}
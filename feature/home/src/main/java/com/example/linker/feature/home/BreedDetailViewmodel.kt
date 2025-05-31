package com.example.linker.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.linker.core.database.model.DetailImageEntity
import com.example.linker.core.model.Image
import com.linker.core.domain.GetBreedsDetailImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BreedDetailViewModel @Inject constructor(
    private val getBreedDetailImagesUseCase: GetBreedsDetailImageUseCase
) : ViewModel() {


    private val _detailImages = MutableStateFlow<PagingData<Image>>(PagingData.empty())
    val detailImages: StateFlow<PagingData<Image>> = _detailImages

    fun loadImages(breedId: String) {
        viewModelScope.launch {
            getBreedDetailImagesUseCase.invoke(breedId)
                .cachedIn(viewModelScope)
                .collect {
                    _detailImages.value = it
                }
        }
    }

}
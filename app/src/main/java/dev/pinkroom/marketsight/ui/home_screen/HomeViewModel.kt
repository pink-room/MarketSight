package dev.pinkroom.marketsight.ui.home_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pinkroom.marketsight.domain.repository.NewsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    newsRepository: NewsRepository
): ViewModel(){
    init {
        viewModelScope.launch {
            newsRepository.subscribeNews().collect{

            }
        }
        viewModelScope.launch {
            newsRepository.getRealTimeNews().collect{

            }
        }
        viewModelScope.launch {
            delay(10000)
            newsRepository.changeFilterNews().collect{

            }
        }
    }
}
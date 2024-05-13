package dev.pinkroom.marketsight.presentation.detail_screen

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pinkroom.marketsight.presentation.core.navigation.Args.SYMBOL_ID
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
): ViewModel() {
    init {
        val id = savedStateHandle.get<String>(SYMBOL_ID)
        Log.d("TESTE",id.toString())
    }
}
package dev.pinkroom.marketsight.di

import androidx.lifecycle.SavedStateHandle
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dev.pinkroom.marketsight.presentation.core.navigation.Args.SYMBOL_ID

@Module
@InstallIn(ViewModelComponent::class)
object DetailScreenArgModule {
    @Provides
    @SymbolId
    @ViewModelScoped
    fun providePersonName(
        savedStatedHandle: SavedStateHandle,
    ): String? {
        return savedStatedHandle[SYMBOL_ID]
    }
    annotation class SymbolId
}
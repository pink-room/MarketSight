package dev.pinkroom.marketsight.di

import androidx.lifecycle.SavedStateHandle
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object DetailScreenArgModule {
    @Provides
    @SymbolId
    @ViewModelScoped
    fun providePersonName(
        savedStatedHandle: SavedStateHandle,
    ): String? {
        return ""//savedStatedHandle[NAME_ARG]
    }
    annotation class SymbolId
}
package com.example.testapplt.domain.interactor

import androidx.paging.PagingData
import com.example.testapplt.domain.model.BooksInfo
import com.example.testapplt.domain.repository.GoogleBookRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class BooksSearchUseCase @Inject constructor(
    private val googleBookRepository: GoogleBookRepository
) {

    companion object {
        private const val DEBOUNCE_INPUT_TIME = 700L
    }

    private val liveSearchQueryWithDebounce = MutableSharedFlow<String>()
    private val liveSearchQueryWithInstantSearch = MutableSharedFlow<String>()

    @ExperimentalCoroutinesApi
    @FlowPreview
    fun getSearchChange(): Flow<PagingData<BooksInfo>> {
        val queryFlow = flowOf(
            liveSearchQueryWithInstantSearch.distinctUntilChanged(),
            liveSearchQueryWithDebounce.debounce(DEBOUNCE_INPUT_TIME).distinctUntilChanged()
        ).flattenMerge()

        return queryFlow.flatMapLatest {
            googleBookRepository.getPagingBooks(it)
        }.distinctUntilChanged()
    }

    suspend fun onBooksSubmit(query: String, isInstantSearch: Boolean) {
        if (isInstantSearch)
            liveSearchQueryWithInstantSearch.emit(query)
        else
            liveSearchQueryWithDebounce.emit(query)
    }
}
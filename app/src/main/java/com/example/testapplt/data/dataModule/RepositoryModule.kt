package com.example.testapplt.data.dataModule

import com.example.testapplt.data.remote.repository.book.GoogleBookRepositoryImp
import com.example.testapplt.domain.repository.GoogleBookRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Singleton
    @Binds
    fun googleBookRepository(googleBookRepositoryImp: GoogleBookRepositoryImp): GoogleBookRepository

}
package com.testing.testforjob.data.posts

import com.testing.testforjob.data.common.module.NetworkModule
import com.testing.testforjob.data.posts.remote.api.PostsApi
import com.testing.testforjob.data.posts.repository.PostsRepositoryImpl
import com.testing.testforjob.domain.posts.PostsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
class PostsModule {
    @Singleton
    @Provides
    fun providePostsApi(retrofit: Retrofit) : PostsApi {
        return retrofit.create(PostsApi::class.java)
    }

    @Singleton
    @Provides
    fun providePostsRepository(postsApi: PostsApi) : PostsRepository {
        return PostsRepositoryImpl(postsApi)
    }
}
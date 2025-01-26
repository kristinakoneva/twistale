package com.kristinakoneva.twistale.domain

import com.kristinakoneva.twistale.domain.game.GameRepository
import com.kristinakoneva.twistale.domain.game.GameRepositoryImpl
import com.kristinakoneva.twistale.domain.user.UserRepository
import com.kristinakoneva.twistale.domain.user.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoriesModule {

    @Binds
    fun userRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Binds
    fun gameRepository(gameRepositoryImpl: GameRepositoryImpl): GameRepository
}

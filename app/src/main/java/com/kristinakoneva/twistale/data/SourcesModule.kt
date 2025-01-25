package com.kristinakoneva.twistale.data

import com.kristinakoneva.twistale.data.auth.AuthSource
import com.kristinakoneva.twistale.data.auth.AuthSourceImpl
import com.kristinakoneva.twistale.data.database.DatabaseSource
import com.kristinakoneva.twistale.data.database.DatabaseSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface SourcesModule {
    @Binds
    fun binduthSource(source: AuthSourceImpl): AuthSource

    @Binds
    fun bindDatabaseSource(source: DatabaseSourceImpl): DatabaseSource
}

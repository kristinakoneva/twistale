package com.kristinakoneva.twistale.data

import com.kristinakoneva.twistale.data.auth.AuthSource
import com.kristinakoneva.twistale.data.auth.AuthSourceImpl
import com.kristinakoneva.twistale.data.database.DatabaseSource
import com.kristinakoneva.twistale.data.database.DatabaseSourceImpl
import com.kristinakoneva.twistale.data.prefs.PreferencesSource
import com.kristinakoneva.twistale.data.prefs.PreferencesSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface SourcesModule {
    @Binds
    fun bindAuthSource(source: AuthSourceImpl): AuthSource

    @Binds
    fun bindDatabaseSource(source: DatabaseSourceImpl): DatabaseSource

    @Binds
    fun bindPreferencesSource(source: PreferencesSourceImpl): PreferencesSource
}

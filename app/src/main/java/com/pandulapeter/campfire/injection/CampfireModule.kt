package com.pandulapeter.campfire.injection

import com.google.gson.GsonBuilder
import com.pandulapeter.campfire.data.repository.SongRepository
import com.pandulapeter.campfire.integration.AppShortcutManager
import com.pandulapeter.campfire.integration.DeepLinkManager
import com.pandulapeter.campfire.networking.AnalyticsManager
import com.pandulapeter.campfire.networking.NetworkManager
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext

val integrationModule: Module = applicationContext {
    provide { AppShortcutManager(get()) }
    provide { DeepLinkManager() }
}

val networkingModule: Module = applicationContext {
    provide { GsonBuilder().create() }
    provide { AnalyticsManager() }
    provide { NetworkManager(get()) }
}

val repositoryModule: Module = applicationContext {
    provide { SongRepository(get()) }
}
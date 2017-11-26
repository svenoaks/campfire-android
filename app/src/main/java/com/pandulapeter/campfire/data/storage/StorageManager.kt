package com.pandulapeter.campfire.data.storage

import android.content.Context
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.pandulapeter.campfire.data.model.SongInfo
import com.pandulapeter.campfire.feature.home.HomeViewModel


/**
 * Wrapper for locally storing simple key-value pairs.
 */
class StorageManager(context: Context, private val gson: Gson) {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)

    /**
     * The last selected item from the home screen's bottom navigation bar.
     */
    var lastSelectedNavigationItem: HomeViewModel.NavigationItem
        get() = when (sharedPreferences.getInt(LAST_SELECTED_NAVIGATION_ITEM, 0)) {
            1 -> HomeViewModel.NavigationItem.DOWNLOADED
            2 -> HomeViewModel.NavigationItem.FAVORITES
            else -> HomeViewModel.NavigationItem.CLOUD
        }
        set(value) {
            sharedPreferences.edit().putInt(LAST_SELECTED_NAVIGATION_ITEM, when (value) {
                HomeViewModel.NavigationItem.CLOUD -> 0
                HomeViewModel.NavigationItem.DOWNLOADED -> 1
                HomeViewModel.NavigationItem.FAVORITES -> 2
            }).apply()
        }

    /**
     * The timestamp of the most recent update that helps to determine how old is the local cache.
     */
    var lastLibraryUpdate: Long
        get() = sharedPreferences.getLong(LAST_LIBRARY_UPDATE, 0)
        set(value) {
            sharedPreferences.edit().putLong(LAST_LIBRARY_UPDATE, value).apply()
        }

    /**
     * The cached list of library items.
     *
     * TODO: This shouldn't be stored in Shared Preferences, replace it with a Room-based implementation.
     */
    var library: List<SongInfo>
        get() = try {
            gson.fromJson(sharedPreferences.getString(LIBRARY, "[]"), object : TypeToken<List<SongInfo>>() {}.type)
        } catch (_: JsonSyntaxException) {
            listOf()
        }
        set(value) {
            sharedPreferences.edit().putString(LIBRARY, gson.toJson(value)).apply()
        }

    /**
     * The list of downloaded songs.
     *
     * //TODO: Only store the list of ID-s.
     */
    var downloaded: List<SongInfo>
        get() = try {
            gson.fromJson(sharedPreferences.getString(DOWNLOADED, "[]"), object : TypeToken<List<SongInfo>>() {}.type)
        } catch (_: JsonSyntaxException) {
            listOf()
        }
        set(value) {
            sharedPreferences.edit().putString(DOWNLOADED, gson.toJson(value)).apply()
        }

    /**
     * The list of song ID-s that the user marked as favorites.
     */
    var favorites: List<String>
        get() = try {
            gson.fromJson(sharedPreferences.getString(FAVORITES, "[]"), object : TypeToken<List<String>>() {}.type)
        } catch (_: JsonSyntaxException) {
            listOf()
        }
        set(value) {
            sharedPreferences.edit().putString(FAVORITES, gson.toJson(value)).apply()
        }

    companion object {
        private const val LAST_SELECTED_NAVIGATION_ITEM = "last_selected_navigation_item"
        private const val LAST_LIBRARY_UPDATE = "last_library_update"
        private const val LIBRARY = "library"
        private const val DOWNLOADED = "downloaded"
        private const val FAVORITES = "favorites"
    }
}
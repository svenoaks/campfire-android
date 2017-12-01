package com.pandulapeter.campfire.feature.home.library

import android.os.Bundle
import android.view.View
import com.pandulapeter.campfire.LibraryBinding
import com.pandulapeter.campfire.R
import com.pandulapeter.campfire.feature.home.shared.HomeFragment

/**
 * Displays the list of all available songs from the backend. The list is searchable and filterable
 * and contains headers. The list is also cached locally and automatically updated after a period or
 * manually using the pull-to-refresh gesture.
 *
 * Controlled by [LibraryViewModel].
 */
class LibraryFragment : HomeFragment<LibraryBinding, LibraryViewModel>(R.layout.fragment_library) {

    override val viewModel by lazy { LibraryViewModel(callbacks, songInfoRepository) }

    //TODO: Add error state for incorrect downloads.
    //TODO: Add no-results state for the case when everything is filtered out.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Setup list item click listeners.
        viewModel.adapter.itemActionClickListener = { position ->
            viewModel.adapter.items[position].let { viewModel.addOrRemoveSongFromDownloads(it.songInfo.id) }
        }
    }

    override fun getRecyclerView() = binding.recyclerView

    override fun getSwipeRefreshLayout() = binding.swipeRefreshLayout

    override fun onBackPressed(): Boolean {
        if (viewModel.searchInputVisible.get()) {
            viewModel.searchInputVisible.set(false)
            return true
        }
        return false
    }
}
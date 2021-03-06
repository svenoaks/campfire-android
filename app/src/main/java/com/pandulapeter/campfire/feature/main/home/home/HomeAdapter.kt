package com.pandulapeter.campfire.feature.main.home.home

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.pandulapeter.campfire.feature.main.collections.CollectionListItemViewHolder
import com.pandulapeter.campfire.feature.main.collections.CollectionListItemViewModel
import com.pandulapeter.campfire.feature.main.shared.baseSongList.SongListItemViewHolder
import com.pandulapeter.campfire.feature.main.shared.baseSongList.SongListItemViewModel

class HomeAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_COLLECTION = 1
        private const val VIEW_TYPE_SONG = 2
    }

    private var recyclerView: RecyclerView? = null
    var shouldScrollToTop = false
    var items = listOf<HomeItemViewModel>()
        set(newItems) {
            val oldItems = items
            DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize() = oldItems.size

                override fun getNewListSize() = newItems.size

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val old = oldItems[oldItemPosition]
                    val new = newItems[newItemPosition]
                    return when (old) {
                        is HomeHeaderViewModel -> when (new) {
                            is HomeHeaderViewModel -> old.title == new.title
                            else -> false
                        }
                        is CollectionListItemViewModel.CollectionViewModel -> when (new) {
                            is CollectionListItemViewModel.CollectionViewModel -> old.collection.id == new.collection.id
                            else -> false
                        }
                        is SongListItemViewModel.SongViewModel -> when (new) {
                            is SongListItemViewModel.SongViewModel -> old.song.id == new.song.id
                            else -> false
                        }
                        else -> false
                    }
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldItems[oldItemPosition] == newItems[newItemPosition]
            }).dispatchUpdatesTo(this@HomeAdapter)
            if (shouldScrollToTop) {
                recyclerView?.run { scrollToPosition(0) }
                shouldScrollToTop = false
            }
            field = newItems
        }
    var collectionClickListener: (position: Int, clickedView: View, image: View) -> Unit = { _, _, _ -> }
    var bookmarkActionClickListener: ((position: Int) -> Unit)? = null
    var songClickListener: (position: Int, clickedView: View) -> Unit = { _, _ -> }
    var playlistActionClickListener: ((position: Int) -> Unit)? = null
    var downloadActionClickListener: ((position: Int) -> Unit)? = null

    init {
        setHasStableIds(true)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = null
    }

    override fun getItemViewType(position: Int) = when (items[position]) {
        is HomeHeaderViewModel -> VIEW_TYPE_HEADER
        is CollectionListItemViewModel.CollectionViewModel -> VIEW_TYPE_COLLECTION
        is SongListItemViewModel.SongViewModel -> VIEW_TYPE_SONG
        else -> throw IllegalArgumentException("Unsupported item type.")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        VIEW_TYPE_HEADER -> HomeHeaderViewHolder.HeaderViewHolder(parent)
        VIEW_TYPE_COLLECTION -> CollectionListItemViewHolder.CollectionViewHolder(parent).apply {
            setItemClickListener(collectionClickListener)
            setSaveActionClickListener(bookmarkActionClickListener)
        }
        VIEW_TYPE_SONG -> SongListItemViewHolder.SongViewHolder(parent).apply {
            setItemClickListener(songClickListener)
            setPlaylistActionClickListener(playlistActionClickListener)
            setDownloadActionClickListener(downloadActionClickListener)
        }
        else -> throw IllegalArgumentException("Unsupported item type.")
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = Unit

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: List<Any>) {
        when (holder) {
            is HomeHeaderViewHolder.HeaderViewHolder -> (items[position] as? HomeHeaderViewModel)?.let { holder.bind(it) }
            is CollectionListItemViewHolder.CollectionViewHolder -> {
                (items[position] as? CollectionListItemViewModel.CollectionViewModel)?.run {
                    payloads.forEach { payload ->
                        when (payload) {
                            is Payload.BookmarkedStateChanged -> collection.isBookmarked = payload.isBookmarked
                        }
                    }
                    holder.bind(this, payloads.isEmpty())
                }
            }
            is SongListItemViewHolder.SongViewHolder -> {
                (items[position] as? SongListItemViewModel.SongViewModel)?.run {
                    payloads.forEach { payload ->
                        when (payload) {
                            is Payload.DownloadStateChanged -> downloadState = payload.downloadState
                            is Payload.IsSongInAPlaylistChanged -> isOnAnyPlaylists = payload.isSongInAPlaylist
                        }
                    }
                    holder.bind(this, payloads.isEmpty())
                }
            }
        }
    }

    override fun getItemCount() = items.size

    override fun getItemId(position: Int) = items[position].getItemId()

    sealed class Payload {
        class BookmarkedStateChanged(val isBookmarked: Boolean) : Payload()
        class DownloadStateChanged(val downloadState: SongListItemViewModel.SongViewModel.DownloadState) : Payload()
        class IsSongInAPlaylistChanged(val isSongInAPlaylist: Boolean) : Payload()
    }
}
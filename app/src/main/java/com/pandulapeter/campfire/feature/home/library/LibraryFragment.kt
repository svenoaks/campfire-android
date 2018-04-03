package com.pandulapeter.campfire.feature.home.library

import android.content.Context
import android.os.Bundle
import android.view.View
import com.pandulapeter.campfire.R
import com.pandulapeter.campfire.feature.home.shared.SongListFragment
import com.pandulapeter.campfire.feature.shared.widget.ToolbarButton
import com.pandulapeter.campfire.feature.shared.widget.ToolbarTextInputView
import com.pandulapeter.campfire.util.BundleArgumentDelegate
import com.pandulapeter.campfire.util.animatedDrawable
import com.pandulapeter.campfire.util.consume
import com.pandulapeter.campfire.util.drawable

class LibraryFragment : SongListFragment<LibraryViewModel>() {

    override val viewModel by lazy {
        LibraryViewModel(
            ToolbarTextInputView(mainActivity.toolbarContext).apply { title.updateToolbarTitle(R.string.home_library) },
            { searchToggle.setImageDrawable((if (it) drawableSearchToClose else drawableCloseToSearch).apply { this?.start() }) },
            { mainActivity.enableSecondaryNavigationDrawer(R.menu.library) }
        )
    }
    private var Bundle.isTextInputVisible by BundleArgumentDelegate.Boolean("isTextInputVisible")
    private var Bundle.searchQuery by BundleArgumentDelegate.String("searchQuery")
    private val searchToggle: ToolbarButton by lazy { mainActivity.toolbarContext.createToolbarButton(R.drawable.ic_search_24dp) { viewModel.toggleTextInputVisibility() } }
    private val drawableCloseToSearch by lazy { context.animatedDrawable(R.drawable.avd_close_to_search_24dp) }
    private val drawableSearchToClose by lazy { context.animatedDrawable(R.drawable.avd_search_to_close_24dp) }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.let {
            if (it.isTextInputVisible) {
                searchToggle.setImageDrawable(context.drawable(R.drawable.ic_close_24dp))
                viewModel.toolbarTextInputView.textInput.run {
                    setText(savedInstanceState.searchQuery)
                    setSelection(text.length)
                }
                viewModel.toolbarTextInputView.showTextInput()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.isTextInputVisible = viewModel.toolbarTextInputView.isTextInputVisible
        outState?.searchQuery = viewModel.query
    }

    override fun onNavigationItemSelected(menuItemId: Int) = when (menuItemId) {
        R.id.downloaded_only -> consume { viewModel.shouldShowDownloadedOnly = !viewModel.shouldShowDownloadedOnly }
        R.id.show_work_in_progress -> consume { viewModel.shouldShowWorkInProgress = !viewModel.shouldShowWorkInProgress }
        R.id.show_explicit -> consume { viewModel.shouldShowExplicit = !viewModel.shouldShowExplicit }
        R.id.sort_by_popularity -> consume { viewModel.sortingMode = LibraryViewModel.SortingMode.POPULARITY }
        R.id.sort_by_title -> consume { viewModel.sortingMode = LibraryViewModel.SortingMode.TITLE }
        R.id.sort_by_artist -> consume { viewModel.sortingMode = LibraryViewModel.SortingMode.ARTIST }
        else -> consume { showSnackbar(R.string.work_in_progress) }//viewModel.languageFilters.get()?.filterKeys { language -> language.nameResource == it.itemId }?.values?.first()?.toggle()
    }

    override fun inflateToolbarTitle(context: Context) = viewModel.toolbarTextInputView

    override fun inflateToolbarButtons(context: Context) = listOf(
        searchToggle,
        context.createToolbarButton(R.drawable.ic_view_options_24dp) { mainActivity.openSecondaryNavigationDrawer() }
    )

    override fun onBackPressed() = if (viewModel.toolbarTextInputView.isTextInputVisible) {
        viewModel.toggleTextInputVisibility()
        true
    } else super.onBackPressed()
}
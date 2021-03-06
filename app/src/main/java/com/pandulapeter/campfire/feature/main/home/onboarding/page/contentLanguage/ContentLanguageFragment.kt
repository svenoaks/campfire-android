package com.pandulapeter.campfire.feature.main.home.onboarding.page.contentLanguage

import android.support.v7.widget.AppCompatCheckBox
import android.view.ViewGroup
import com.pandulapeter.campfire.R
import com.pandulapeter.campfire.data.persistence.PreferenceDatabase
import com.pandulapeter.campfire.databinding.FragmentOnboardingContentLanguageBinding
import com.pandulapeter.campfire.feature.main.home.onboarding.OnboardingFragment
import com.pandulapeter.campfire.feature.main.home.onboarding.page.OnboardingPageFragment
import com.pandulapeter.campfire.util.dimension
import org.koin.android.ext.android.inject

class ContentLanguageFragment : OnboardingPageFragment<FragmentOnboardingContentLanguageBinding, ContentLanguageViewModel>(R.layout.fragment_onboarding_content_language) {

    override val viewModel = ContentLanguageViewModel {
        if (isAdded) {
            totalLanguageCount = it.size
            binding.languageContainer.apply {
                removeAllViews()
                val contentPadding = context.dimension(R.dimen.content_padding)
                it.forEach {
                    addView(AppCompatCheckBox(getCampfireActivity()).apply {
                        setText(it.nameResource)
                        setPadding(contentPadding, 0, 0, 0)
                        isChecked = !preferenceDatabase.disabledLanguageFilters.contains(it.id)
                        setOnCheckedChangeListener { _, _ ->
                            preferenceDatabase.disabledLanguageFilters =
                                    preferenceDatabase.disabledLanguageFilters.toMutableSet().apply { if (contains(it.id)) remove(it.id) else add(it.id) }
                            onLanguageFiltersUpdated()
                        }
                    }, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                }
            }
            onLanguageFiltersUpdated()
        }
    }
    private val preferenceDatabase by inject<PreferenceDatabase>()
    private var totalLanguageCount = 0

    private fun onLanguageFiltersUpdated() {
        (totalLanguageCount - preferenceDatabase.disabledLanguageFilters.size).let { selectedLanguageCount ->
            (parentFragment as OnboardingFragment).languageFiltersUpdated(selectedLanguageCount)
            viewModel.shouldShowError.set(selectedLanguageCount == 0)
        }
    }
}
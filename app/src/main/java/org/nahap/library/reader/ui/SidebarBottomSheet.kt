package org.nahap.library.reader.ui

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.tabs.TabLayoutMediator
import org.nahap.library.databinding.BottomSheetSidebarBinding
import org.nahap.library.reader.ui.adapter.SidebarPagerAdapter
import org.nahap.library.reader.viewmodel.ReaderViewModel

class SidebarBottomSheet(
    context: Context,
    private val viewModel: ReaderViewModel,
    private val webView: ReaderWebView,
    private val initialTab: Int = 0,
    private val onTocItemClick: (String) -> Unit
) : LinearLayout(context) {

    private val binding: BottomSheetSidebarBinding

    init {
        binding = BottomSheetSidebarBinding.inflate(LayoutInflater.from(context), this, true)

        setupHeader()
        setupTabs()
    }

    private fun setupHeader() {
        viewModel.state.value.let { state ->
            binding.tvBookTitle.text = state.title.ifEmpty { "Загрузка..." }
            binding.tvBookAuthor.text = state.author.ifEmpty { "" }
        }
    }

    private fun setupTabs() {
        val lifecycleOwner = when (context) {
            is LifecycleOwner -> context as LifecycleOwner
            else -> null
        }
        
        val adapter = SidebarPagerAdapter(
            context = context,
            viewModel = viewModel,
            lifecycleOwner = lifecycleOwner,
            onTocItemClick = { elementId ->
                webView.scrollToElement(elementId)
                onTocItemClick(elementId)
            },
            onBookmarkClick = { position ->
                webView.scrollToFraction(position)
            }
        )

        binding.viewPager.adapter = adapter
        binding.viewPager.offscreenPageLimit = 2

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Оглавление"
                1 -> "Закладки"
                else -> ""
            }
        }.attach()

        binding.viewPager.setCurrentItem(initialTab, false)
    }
}
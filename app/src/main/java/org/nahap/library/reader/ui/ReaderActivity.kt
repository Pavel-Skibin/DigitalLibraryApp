package org.nahap.library.reader.ui

import android.os.Bundle
import android.view. Menu
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.nahap.library.R
import org.nahap.library.databinding.ActivityReaderBinding
import org.nahap.library.reader.model.ReaderState
import org.nahap.library. reader.model.ReadingSettings
import org.nahap.library.reader.viewmodel.ReaderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.round

@AndroidEntryPoint
class ReaderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReaderBinding
    private val viewModel: ReaderViewModel by viewModels()

    private var sidebarDialog: BottomSheetDialog?  = null
    private var isUserScrolling = false
    private var bookmarkCounter = 1
    private var isContentLoaded = false
    private var lastAppliedSettings: ReadingSettings?  = null

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater. inflate(R.menu.menu_reader, menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReaderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bookId = intent.getIntExtra(EXTRA_BOOK_ID, -1)

        if (bookId == -1) {
            bookId = 1
            Toast.makeText(this, "Используется bookId по умолчанию: $bookId", Toast.LENGTH_SHORT).show()
        }

        setupToolbar()
        setupWebView()
        setupBottomBar()
        setupObservers()

        viewModel.loadBook(bookId)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_toc -> {
                    showSidebar(0)
                    true
                }
                R.id. action_settings -> {
                    showReadingSettingsDialog()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupWebView() {
        binding.webView. setOnPositionChangedListener { position ->
            if (!isUserScrolling) {
                try {
                    val stepSize = 0.0001
                    val preciseValue = round(position / stepSize) * stepSize
                    val roundedValue = round(preciseValue * 10000) / 10000.0
                    val clampedValue = roundedValue. coerceIn(0.0, 1.0)

                    binding.progressSlider.value = clampedValue.toFloat()
                } catch (e: Exception) {
                    binding. progressSlider.value = position. toFloat().coerceIn(0f, 1f)
                }
            }
            viewModel.updatePosition(position)
        }

        binding.webView.setOnPageInfoChangedListener { pageInfo ->
            binding.tvPageInfo.text = pageInfo
            viewModel.updatePageInfo(pageInfo)
        }
    }

    private fun setupBottomBar() {
        binding. progressSlider.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                isUserScrolling = true
                binding.webView.scrollToFraction(value.toDouble())
                binding.webView.postDelayed({
                    isUserScrolling = false
                }, 500)
            }
        }

    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                updateUI(state)
            }
        }
    }

    private fun updateUI(state: ReaderState) {

        binding.progressLoading.visibility = if (state.isLoading) View.VISIBLE else View.GONE
        binding.webView.visibility = if (state. isLoading) View.GONE else View.VISIBLE

        if (state.error != null) {
            Toast.makeText(this, state. error, Toast.LENGTH_LONG).show()
        }

        if (state.title. isNotEmpty()) {
            binding.toolbar.title = state.title
        }

        if (state.htmlContent.isNotEmpty() && !isContentLoaded) {
            isContentLoaded = true
            binding.webView.loadBookContent(state.htmlContent)
        }

        if (state.bookmarks.isNotEmpty()) {
            val maxNumber = state.bookmarks.mapNotNull { bookmark ->
                bookmark.name.removePrefix("Закладка ").toIntOrNull()
            }.maxOrNull() ?: 0
            bookmarkCounter = maxNumber + 1
        }
    }

    private fun showSidebar(tabIndex: Int) {
        val dialog = BottomSheetDialog(this)
        val sidebarView = SidebarBottomSheet(
            context = this,
            viewModel = viewModel,
            webView = binding.webView,
            initialTab = tabIndex,
            onTocItemClick = { elementId: String ->
                if (elementId. isNotEmpty()) {
                    binding.webView.scrollToElement(elementId)
                }
                dialog.dismiss()
            }
        )

        dialog.setContentView(sidebarView)
        dialog. show()
        sidebarDialog = dialog
    }

    private fun addBookmark() {
        binding.webView.getCurrentPosition { position ->
            val name = "Закладка $bookmarkCounter"
            viewModel.createBookmark(position, name, "")
            bookmarkCounter++
            Toast.makeText(this, "Закладка добавлена", Toast.LENGTH_SHORT).show()
            showSidebar(1)
        }
    }

    private fun showReadingSettingsDialog() {
        val currentSettings = viewModel.state.value.readingSettings
        ReadingSettingsDialog.show(
            context = this,
            settings = currentSettings,
            onSettingsChanged = { newSettings: ReadingSettings ->
                viewModel.updateReadingSettings(newSettings)
                binding.webView.applySettings(newSettings)
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        sidebarDialog?.dismiss()
    }

    companion object {
        const val EXTRA_BOOK_ID = "BOOK_ID"
    }
}
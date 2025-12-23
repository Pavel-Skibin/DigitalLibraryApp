package org. nahap.library.catalog.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx. activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil. load
import coil.transform. RoundedCornersTransformation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.nahap.library.R
import org.nahap.library.catalog.ui.adapter.CommentAdapter
import org.nahap.library.catalog.viewmodel.BookDetailViewModel
import org.nahap.library.databinding.ActivityBookDetailBinding
import org.nahap.library.reader.ui.ReaderActivity

@AndroidEntryPoint
class BookDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_BOOK_ID = "book_id"
    }



    private lateinit var binding: ActivityBookDetailBinding
    private val viewModel: BookDetailViewModel by viewModels()
    private val commentAdapter = CommentAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding. root)

        val bookId = intent.getIntExtra(EXTRA_BOOK_ID, -1)
        if (bookId == -1) {
            Toast.makeText(this, "Книга не найдена", Toast. LENGTH_SHORT).show()
            finish()
            return
        }

        setupToolbar()
        setupComments()
        setupObservers()
        setupReadButton(bookId)

        viewModel.loadBookDetails(bookId)
    }

    private fun setupToolbar() {
        binding.toolbar. setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupComments() {
        binding.recyclerViewComments.adapter = commentAdapter
    }

    private fun setupReadButton(bookId: Int) {
        binding.btnRead.setOnClickListener {
            val intent = Intent(this, ReaderActivity::class.java).apply {
                putExtra(ReaderActivity. EXTRA_BOOK_ID, bookId)
            }
            startActivity(intent)
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                binding.progressLoading.visibility = if (state. isLoading) View.VISIBLE else View.GONE

                state.error?.let {
                    Toast.makeText(this@BookDetailActivity, it, Toast. LENGTH_LONG).show()
                }

                state.book?.let { book ->
                    binding.toolbar.title = book.title
                    binding.tvTitle.text = book.title
                    binding.tvAuthors.text = book.authors?.joinToString(", ") ?: "Автор неизвестен"
                    binding.tvGenres.text = book.genres?.joinToString(", ") ?: ""
                    binding.tvDescription. text = book.description ?: "Описание отсутствует"

                    if (book.averageRating != null && book.averageRating > 0) {
                        binding.tvRating.text = String.format("%.1f", book.averageRating)
                        binding.tvRatingsCount.text = "(${book.totalRatings ?: 0} оценок)"
                    } else {
                        binding.tvRating.text = "—"
                        binding.tvRatingsCount.text = "(нет оценок)"
                    }

                    val coverUrl = viewModel.getBookCoverUrl(book.id)
                    binding.ivBookCover.load(coverUrl) {
                        crossfade(true)
                        placeholder(R.drawable.ic_book_placeholder)
                        error(R.drawable.ic_book_placeholder)
                        transformations(RoundedCornersTransformation(8f))
                    }

                    val activeComments = book.comments?.filter { it.deletedAt == null } ?: emptyList()
                    if (activeComments.isEmpty()) {
                        binding.tvNoComments.visibility = View. VISIBLE
                        binding.recyclerViewComments.visibility = View.GONE
                    } else {
                        binding.tvNoComments.visibility = View. GONE
                        binding.recyclerViewComments.visibility = View. VISIBLE
                        commentAdapter. submitList(activeComments)
                    }
                }
            }
        }
    }
}
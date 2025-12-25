package org.nahap.library.reader.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.nahap.library.databinding.DialogEditBookmarkBinding
import org.nahap.library.reader.model.BookmarkResponse


class EditBookmarkDialog(
    context: Context,
    private val bookmark: BookmarkResponse,
    private val onSave: (String, String) -> Unit,
    private val onDelete: () -> Unit
) : Dialog(context) {

    private lateinit var binding: DialogEditBookmarkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DialogEditBookmarkBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        binding.etBookmarkName.setText(bookmark.name)
        binding.etBookmarkNotes.setText(bookmark.notes ?: "")
    }

    private fun setupListeners() {

        binding.btnSave.setOnClickListener {
            val name = binding.etBookmarkName.text.toString().trim()
            val notes = binding.etBookmarkNotes.text.toString().trim()

            if (name.isEmpty()) {
                binding.etBookmarkName.error = "Введите название"
                return@setOnClickListener
            }

            onSave(name, notes)
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnDelete.setOnClickListener {
            MaterialAlertDialogBuilder(context)
                .setTitle("Удалить закладку?")
                .setMessage("Вы уверены, что хотите удалить закладку \"${bookmark.name}\"?")
                .setPositiveButton("Удалить") { _, _ ->
                    onDelete()
                    dismiss()
                }
                .setNegativeButton("Отмена", null)
                .show()
        }
    }

    companion object {
        fun show(
            context: Context,
            bookmark: BookmarkResponse,
            onSave: (String, String) -> Unit,
            onDelete: () -> Unit
        ) {
            EditBookmarkDialog(context, bookmark, onSave, onDelete).show()
        }
    }
}
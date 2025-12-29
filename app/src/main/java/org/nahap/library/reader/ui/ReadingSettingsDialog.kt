package org.nahap.library.reader.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import org.nahap.library.R
import org.nahap.library.databinding.DialogReadingSettingsBinding
import org.nahap.library.reader.model.ReadingSettings

class ReadingSettingsDialog(
    context: Context,
    private val initialSettings: ReadingSettings,
    private val onSettingsChanged: (ReadingSettings) -> Unit
) : Dialog(context) {

    private lateinit var binding: DialogReadingSettingsBinding
    private var currentSettings: ReadingSettings = initialSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DialogReadingSettingsBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)

        window?.apply {
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            setDimAmount(0.3f)
        }

        setupUI()
        setupListeners()
    }

    private fun setupUI() {


        binding.tvFontSize.text = "${currentSettings.fontSize.toInt()}px"
        binding.tvLineHeight.text = String.format("%.1f", currentSettings.lineHeight)
        binding.tvWordSpacing.text = String.format("%.2fem", currentSettings.wordSpacing)

    }

    private fun applyAndNotify() {
        Log.d(TAG, " Sending settings: fontSize=${currentSettings.fontSize}")
        onSettingsChanged(currentSettings)
    }

    private fun setupListeners() {

        binding.flowToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val flow = when (checkedId) {
                    R.id.btnScrolled -> ReadingSettings.ReadingFlow.SCROLLED
                    else -> return@addOnButtonCheckedListener
                }
                currentSettings = currentSettings.copy(flow = flow)
                applyAndNotify()
            }
        }


        binding.btnIncreaseFont.setOnClickListener {
            val newSize = (currentSettings.fontSize + 2).coerceAtMost(32f)
            Log.d(TAG, "⬆Font: ${currentSettings.fontSize} -> $newSize")
            currentSettings = currentSettings.copy(fontSize = newSize)
            binding.tvFontSize.text = "${newSize.toInt()}px"
            applyAndNotify()
        }

        binding.btnDecreaseFont.setOnClickListener {
            val newSize = (currentSettings.fontSize - 2).coerceAtLeast(8f)
            Log.d(TAG, "⬇Font: ${currentSettings.fontSize} -> $newSize")
            currentSettings = currentSettings.copy(fontSize = newSize)
            binding.tvFontSize.text = "${newSize.toInt()}px"
            applyAndNotify()
        }

        binding.btnIncreaseLineHeight.setOnClickListener {
            val newHeight = (currentSettings.lineHeight + 0.1f).coerceAtMost(2.5f)
            currentSettings = currentSettings.copy(lineHeight = newHeight)
            binding.tvLineHeight.text = String.format("%.1f", newHeight)
            applyAndNotify()
        }

        binding.btnDecreaseLineHeight.setOnClickListener {
            val newHeight = (currentSettings.lineHeight - 0.1f).coerceAtLeast(1.0f)
            currentSettings = currentSettings.copy(lineHeight = newHeight)
            binding.tvLineHeight.text = String.format("%.1f", newHeight)
            applyAndNotify()
        }

        binding.btnIncreaseWordSpacing.setOnClickListener {
            val newSpacing = (currentSettings.wordSpacing + 0.05f).coerceAtMost(0.5f)
            currentSettings = currentSettings.copy(wordSpacing = newSpacing)
            binding.tvWordSpacing.text = String.format("%.2fem", newSpacing)
            applyAndNotify()
        }

        binding.btnDecreaseWordSpacing.setOnClickListener {
            val newSpacing = (currentSettings.wordSpacing - 0.05f).coerceAtLeast(0f)
            currentSettings = currentSettings.copy(wordSpacing = newSpacing)
            binding.tvWordSpacing.text = String.format("%.2fem", newSpacing)
            applyAndNotify()
        }


        binding.themeToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val theme = when (checkedId) {
                    R.id.btnLightTheme -> ReadingSettings.ReadingTheme.LIGHT
                    else -> return@addOnButtonCheckedListener
                }
                currentSettings = currentSettings.copy(theme = theme)
                applyAndNotify()
            }
        }
    }


    companion object {
        private const val TAG = "ReadingSettingsDialog"

        fun show(
            context: Context,
            settings: ReadingSettings,
            onSettingsChanged: (ReadingSettings) -> Unit
        ) {
            ReadingSettingsDialog(context, settings, onSettingsChanged).show()
        }
    }
}
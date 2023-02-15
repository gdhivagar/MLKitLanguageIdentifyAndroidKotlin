package com.gdhivagar.mlkitlangid

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.gdhivagar.mlkitlangid.databinding.ActivityLangIdBinding
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentifier

class LangIdActivity : AppCompatActivity() {

    lateinit var binding: ActivityLangIdBinding
    private lateinit var languageIdentifier: LanguageIdentifier

    companion object {
        private const val TAG = "MyActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLangIdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        languageIdentifier = LanguageIdentification.getClient()
        lifecycle.addObserver(languageIdentifier)

        binding.btnIdentifyLang.setOnClickListener {
            hideKeyboard()
            val input = getInputText()
            if (input.isEmpty()) {
                return@setOnClickListener
            }
            identifyLanguageCode(input)
        }

        binding.btnClearText.setOnClickListener {
            binding.etInput.text.clear()
            binding.tvOutput.text = ""
        }
    }

    /**
     * Identify a language.
     *
     * @param inputText Input string to find language of.
     */
    private fun identifyLanguageCode(inputText: String) {
        binding.tvOutput.text = getString(R.string.wait_message)
        languageIdentifier
            .identifyLanguage(inputText)
            .addOnSuccessListener { identifiedLanguage ->
                if (identifiedLanguage == "und") {
                    binding.tvOutput.text = ""
                    Toast.makeText(
                        this@LangIdActivity,
                        getString(R.string.language_id_error),
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    binding.tvOutput.text = getString(R.string.language, identifiedLanguage)
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Language identification error", e)
                binding.tvOutput.text = ""
                Toast.makeText(
                    this@LangIdActivity,
                    getString(R.string.language_id_error) + "\nError: " + e.getLocalizedMessage() + "\nCause: " + e.cause,
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun getInputText(): String {
        val input = binding.etInput.text.toString()
        if (input.isEmpty()) {
            Toast.makeText(this@LangIdActivity, R.string.empty_text_message, Toast.LENGTH_LONG)
                .show()
            return input
        }
        return input
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus
        if (inputMethodManager != null && view != null) {
            inputMethodManager.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

}
package com.jfdedit3.ter

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jfdedit3.ter.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val terminalEngine = TerminalEngine()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.outputText.text = terminalEngine.bootText()

        binding.sendButton.setOnClickListener {
            submitCommand()
        }

        binding.inputEditText.setOnEditorActionListener { _, _, _ ->
            submitCommand()
            true
        }

        binding.headerTitle.setOnLongClickListener {
            Toast.makeText(this, "Ter terminal ready", Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun submitCommand() {
        val command = binding.inputEditText.text?.toString().orEmpty()
        val rendered = terminalEngine.execute(command)
        binding.outputText.text = rendered
        binding.inputEditText.text?.clear()
        binding.outputScrollView.post {
            binding.outputScrollView.fullScroll(android.view.View.FOCUS_DOWN)
        }
    }
}

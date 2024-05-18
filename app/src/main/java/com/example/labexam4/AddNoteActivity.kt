package com.example.labexam4

import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.set
import com.example.labexam4.databinding.ActivityAddNoteBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.graphics.Typeface
import android.view.ViewGroup
import android.widget.GridLayout

class AddNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var db: NoteDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = NoteDatabaseHelper(this)

        binding.saveButton.setOnClickListener {
            val title = binding.titleEditText.text.toString()
            val content = binding.contentEditText.text.toString()
            val note = Note(0, title, content)
            db.insertNote(note)
            finish()
            Toast.makeText(this, "Note Saved", Toast.LENGTH_SHORT).show()
        }

        binding.actionBold.setOnClickListener {
            toggleStyleSpan(binding.contentEditText, StyleSpan(Typeface.BOLD))
        }

        binding.actionItalic.setOnClickListener {
            toggleStyleSpan(binding.contentEditText, StyleSpan(Typeface.ITALIC))
        }

        binding.actionUnderline.setOnClickListener {
            toggleStyleSpan(binding.contentEditText, UnderlineSpan())
        }

        binding.actionBullets.setOnClickListener {
            startBulletsList(binding.contentEditText)
        }

        binding.actionInsertTable.setOnClickListener {
            showTableDialog(binding.contentEditText)
        }
    }

    private fun toggleStyleSpan(editText: EditText, span: Any) {
        val start = editText.selectionStart
        val end = editText.selectionEnd
        val spannable: Editable = editText.text

        if (start != end) {
            val spans = spannable.getSpans(start, end, span::class.java)
            var alreadyApplied = false

            for (s in spans) {
                if (spannable.getSpanStart(s) == start && spannable.getSpanEnd(s) == end) {
                    spannable.removeSpan(s)
                    alreadyApplied = true
                }
            }

            if (!alreadyApplied) {
                spannable.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }

    private fun startBulletsList(typeBox: EditText) {
        val bulletPoint = "\u2022 " // Unicode character for bullet point
        val currentText = typeBox.text.toString()

        if (currentText.isNotEmpty()) {
            typeBox.append("\n")
        }

        typeBox.append(bulletPoint)
    }

    private fun showTableDialog(editText: EditText) {
        val context = this
        val dialogView = layoutInflater.inflate(R.layout.activity_table_input_dialog, null)
        MaterialAlertDialogBuilder(context)
            .setTitle("Insert Table")
            .setMessage("Specify the number of rows and columns")
            .setView(dialogView)
            .setPositiveButton("Insert") { _, _ ->
                val rowsInput = dialogView.findViewById<EditText>(R.id.rowsInput)
                val columnsInput = dialogView.findViewById<EditText>(R.id.columnsInput)
                val rows = rowsInput?.text.toString().toIntOrNull() ?: 0
                val columns = columnsInput?.text.toString().toIntOrNull() ?: 0

                if (rows > 0 && columns > 0) {
                    insertTable(editText, rows, columns)
                } else {
                    Toast.makeText(context, "Invalid input", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun insertTable(editText: EditText, rows: Int, columns: Int) {
        val tableLayout = GridLayout(this).apply {
            rowCount = rows
            columnCount = columns
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        for (i in 0 until rows) {
            for (j in 0 until columns) {
                val cell = EditText(this).apply {
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = 0
                        height = ViewGroup.LayoutParams.WRAP_CONTENT
                        columnSpec = GridLayout.spec(j, 1f)
                        rowSpec = GridLayout.spec(i, 1f)
                    }
                    setPadding(8, 8, 8, 8)
                    background = resources.getDrawable(R.drawable.cell_border, null)
                }
                tableLayout.addView(cell)
            }
        }

        // Add the table to the main layout (or to a specific container)
        binding.contentEditText.append("\n")
        binding.content.addView(tableLayout) // Ensure you have a mainLayout in your Activity's layout to contain the table
    }
}

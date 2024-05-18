package com.example.labexam4

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.ViewGroup
import android.widget.EditText
import android.widget.GridLayout
import android.widget.Toast
import com.example.labexam4.databinding.ActivityUpdateNoteBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class UpdateNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateNoteBinding
    private lateinit var db: NoteDatabaseHelper
    private var noteId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = NoteDatabaseHelper(this)

        noteId = intent.getIntExtra("note_id", -1)
        if (noteId == -1) {
            finish()
            return
        }

        val note = db.getNoteById(noteId)
        binding.updatetitleEditText.setText(note.title)
        binding.updatecontentEditText.setText(note.content)

        binding.updatesaveButton.setOnClickListener {
            val newTitle = binding.updatetitleEditText.text.toString()
            val newContent = binding.updatecontentEditText.text.toString()
            val updateNote = Note(noteId, newTitle, newContent)
            db.updateNote(updateNote)
            finish()
            Toast.makeText(this,"Changes Saved", Toast.LENGTH_SHORT).show()
        }

        binding.actionBold.setOnClickListener {
            toggleStyleSpan(binding.updatecontentEditText, StyleSpan(Typeface.BOLD))
        }

        binding.actionItalic.setOnClickListener {
            toggleStyleSpan(binding.updatecontentEditText, StyleSpan(Typeface.ITALIC))
        }

        binding.actionUnderline.setOnClickListener {
            toggleStyleSpan(binding.updatecontentEditText, UnderlineSpan())
        }

        binding.actionBullets.setOnClickListener {
            startBulletsList(binding.updatecontentEditText)
        }

        binding.actionInsertTable.setOnClickListener {
            showTableDialog(binding.updatecontentEditText)
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
                    background = resources.getDrawable(R.drawable.cell_border, null) // Assuming R.drawable.cell_border exists
                }
                tableLayout.addView(cell)
            }
        }

        // Add the table to the main layout (or to a specific container)
        binding.updatecontentEditText.append("\n")
        binding.updatecontent.addView(tableLayout)
    }
}

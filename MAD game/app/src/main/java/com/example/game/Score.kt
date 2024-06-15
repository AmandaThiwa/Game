package com.example.game

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class Score : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        val exitBtn = findViewById<Button>(R.id.button_ex)

        exitBtn.setOnClickListener {
            val intent = Intent(this@Score, Startpage::class.java)
            startActivity(intent)
        }

        val highScoresText = findViewById<TextView>(R.id.high_scores_text)

        // Retrieve and display high scores
        val highScores = getHighScores()
        highScoresText.text = if (highScores.isEmpty()) {
            "No high scores yet."
        } else {
            highScores.joinToString("\n") { score -> "High Score: $score" } // Display high scores
        }
    }

    private fun getHighScores(): List<Int> {
        val sharedPrefs = getSharedPreferences("HighScores", MODE_PRIVATE)
        val highScores = mutableListOf<Int>()
        for (i in 1..5) {
            highScores.add(sharedPrefs.getInt("score_$i", 0))
        }
        return highScores.filter { it > 0 }.sortedDescending()
    }
}

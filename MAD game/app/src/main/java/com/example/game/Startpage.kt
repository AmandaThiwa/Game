package com.example.game


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Startpage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_startpage)

        val Game = findViewById<Button>(R.id.button)
        val HighScore = findViewById<Button>(R.id.button3)



       Game.setOnClickListener{
            val intent = Intent(this@Startpage,MainActivity::class.java)
            startActivity(intent)

        }
        HighScore.setOnClickListener{
            val intent = Intent(this@Startpage,Score::class.java)
            startActivity(intent)

        }

    }
}
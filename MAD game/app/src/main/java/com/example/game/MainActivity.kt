package com.example.game

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var scoreText: TextView
    private lateinit var paddle: View
    private lateinit var ball: View
    private lateinit var brickContainer: LinearLayout

    private var ballX = 0f
    private var ballY = 0f
    private var ballSpeedX = 0f
    private var ballSpeedY = 0f
    private var paddleX = 0f
    private var score = 0
    private val brickRows = 9
    private val brickColumns = 10
    private val brickWidth = 100
    private val brickHeight = 40
    private val brickMargin = 4
    private var lives = 3
    private var gameOver = false // Flag to track game over state

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scoreText = findViewById(R.id.scoreText)
        paddle = findViewById(R.id.paddle)
        ball = findViewById(R.id.ball)
        brickContainer = findViewById(R.id.brickContainer)

        val newgame = findViewById<Button>(R.id.newgame)
        val Exite = findViewById<Button>(R.id.newgame2)

        newgame.setOnClickListener {
            initializeBricks()
            start()
            newgame.visibility = View.INVISIBLE
            Exite.visibility = View.INVISIBLE
            gameOver = false // Reset game over flag
            score = 0 // Reset score
            scoreText.text = "Score: $score" // Update score display
            lives = 3 // Reset lives
        }

        Exite.setOnClickListener {
            val intent = Intent(this@MainActivity, Startpage::class.java)
            startActivity(intent)
        }
    }

    private fun initializeBricks() {
        val brickWidthWithMargin = (brickWidth + brickMargin).toInt()

        for (row in 0 until brickRows) {
            val rowLayout = LinearLayout(this)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            rowLayout.layoutParams = params

            for (col in 0 until brickColumns) {
                val brick = View(this)
                val brickParams = LinearLayout.LayoutParams(brickWidth, brickHeight)
                brickParams.setMargins(brickMargin, brickMargin, brickMargin, brickMargin)
                brick.layoutParams = brickParams
                brick.setBackgroundResource(R.drawable.ic_launcher_background)
                rowLayout.addView(brick)
            }

            brickContainer.addView(rowLayout)
        }
    }

    private fun moveBall() {
        ballX += ballSpeedX
        ballY += ballSpeedY

        ball.x = ballX
        ball.y = ballY
    }

    private fun movePaddle(x: Float) {
        paddleX = x - paddle.width / 2
        paddle.x = paddleX
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun checkCollision() {
        if (!gameOver) {
            // Check collision with walls
            val screenWidth = resources.displayMetrics.widthPixels.toFloat()
            val screenHeight = resources.displayMetrics.heightPixels.toFloat()

            if (ballX <= 0 || ballX + ball.width >= screenWidth) {
                ballSpeedX *= -1
            }

            if (ballY <= 0) {
                ballSpeedY *= -1
            }

            // Check collision with bricks
            for (row in 0 until brickRows) {
                val rowLayout = brickContainer.getChildAt(row) as LinearLayout
                val rowTop = rowLayout.y + brickContainer.y
                val rowBottom = rowTop + rowLayout.height

                for (col in 0 until brickColumns) {
                    val brick = rowLayout.getChildAt(col) as View

                    if (brick.visibility == View.VISIBLE) {
                        val brickLeft = brick.x + rowLayout.x
                        val brickRight = brickLeft + brick.width
                        val brickTop = brick.y + rowTop
                        val brickBottom = brickTop + brick.height

                        if (ballX + ball.width >= brickLeft && ballX <= brickRight
                            && ballY + ball.height >= brickTop && ballY <= brickBottom
                        ) {
                            brick.visibility = View.INVISIBLE
                            ballSpeedY *= -1
                            score++
                            scoreText.text = "Score: $score"
                            return
                        }
                    }
                }
            }

            // Check collision with bottom wall (paddle misses the ball)
            if (ballY + ball.height >= screenHeight - 100) {
                lives--

                if (lives > 0) {
                    Toast.makeText(this, "$lives balls left ", Toast.LENGTH_SHORT).show()
                }

                if (lives <= 0) {
                    gameOver = true
                    gameOver()
                } else {
                    resetBallPosition()
                    start()
                }
            }

            // Check collision with paddle
            if (ballY + ball.height >= paddle.y && ballY + ball.height <= paddle.y + paddle.height
                && ballX + ball.width >= paddle.x && ballX <= paddle.x + paddle.width
            ) {
                ballSpeedY *= -1
            }
        }
    }

    private fun resetBallPosition() {
        val displayMetrics = resources.displayMetrics
        val screenDensity = displayMetrics.density

        val screenWidth = displayMetrics.widthPixels.toFloat()
        val screenHeight = displayMetrics.heightPixels.toFloat()

        ballX = screenWidth / 2 - ball.width / 2
        ballY = screenHeight / 2 - ball.height / 2 + 525

        ball.x = ballX
        ball.y = ballY

        ballSpeedX = 0 * screenDensity
        ballSpeedY = 0 * screenDensity

        paddleX = screenWidth / 2 - paddle.width / 2
        paddle.x = paddleX
    }

    private fun saveHighScore() {
        val sharedPrefs = getSharedPreferences("HighScores", MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        val scores = mutableListOf<Int>()
        for (i in 1..5) {
            scores.add(sharedPrefs.getInt("score_$i", 0))
        }
        scores.add(score)
        val sortedScores = scores.sortedDescending().take(5)
        for ((index, s) in sortedScores.withIndex()) {
            editor.putInt("score_${index + 1}", s)
        }
        editor.apply()
    }

    private fun gameOver() {

            scoreText.text = "Game Over. Score: $score"
            val newgame = findViewById<Button>(R.id.newgame)
            val Exite = findViewById<Button>(R.id.newgame2)
            saveHighScore()
            newgame.visibility = View.VISIBLE
            Exite.visibility = View.VISIBLE
            Toast.makeText(this, "Game Over. Score: $score", Toast.LENGTH_SHORT).show()
            gameOver = true
        
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun movepaddle() {
        paddle.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    movePaddle(event.rawX)
                }
            }
            true
        }
    }

    private fun start() {
        movepaddle()
        val displayMetrics = resources.displayMetrics
        val screenDensity = displayMetrics.density

        val screenWidth = displayMetrics.widthPixels.toFloat()
        val screenHeight = displayMetrics.heightPixels.toFloat()

        paddleX = screenWidth / 2 - paddle.width / 2
        paddle.x = paddleX

        ballX = screenWidth / 2 - ball.width / 2
        ballY = screenHeight / 2 - ball.height / 2

        val brickHeightWithMargin = (brickHeight + brickMargin * screenDensity).toInt()

        ballSpeedX = 5 * screenDensity
        ballSpeedY = -5 * screenDensity

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = Long.MAX_VALUE
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener { animation ->
            moveBall()
            checkCollision()
        }
        animator.start()
    }
}

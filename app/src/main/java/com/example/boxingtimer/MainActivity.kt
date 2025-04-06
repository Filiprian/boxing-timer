package com.example.boxingtimer

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var progressCircle: ProgressBar
    private lateinit var timerText: TextView
    private lateinit var startStopButton: Button
    private lateinit var resetButton: Button
    private lateinit var roundTimeInput: EditText
    private lateinit var breakTimeInput: EditText
    private lateinit var roundsInput: EditText
    private lateinit var roundCounter: TextView
    private lateinit var type: TextView
    private lateinit var getReady: TextView
    private lateinit var roundTime2: TextView
    private lateinit var breakTime2: TextView
    private lateinit var rounds2: TextView
    private lateinit var roundTime2Text: TextView
    private lateinit var breakTime2Text: TextView
    private lateinit var rounds2Text: TextView

    private var roundTime: Long = 0L
    private var breakTime: Long = 0L
    private var totalRounds: Int = 0
    private var currentRound: Int = 0
    private var isRunning = false
    private var isRoundPhase = true
    private var timer: CountDownTimer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        progressCircle = findViewById(R.id.progressBar)
        timerText = findViewById(R.id.ttCountdown)
        startStopButton = findViewById(R.id.btStart)
        resetButton = findViewById(R.id.btReset)
        roundTimeInput = findViewById(R.id.inRound)
        breakTimeInput = findViewById(R.id.inBreak)
        roundsInput = findViewById(R.id.inRounds)
        roundCounter = findViewById(R.id.ttCurrentRound)
        type = findViewById(R.id.ttFight)
        getReady = findViewById(R.id.ttGetReady)
        roundTime2 = findViewById(R.id.ttRound)
        breakTime2 = findViewById(R.id.ttBreak)
        rounds2 = findViewById(R.id.ttRounds)
        roundTime2Text = findViewById(R.id.ttRoundText)
        breakTime2Text = findViewById(R.id.ttBreakText)
        rounds2Text = findViewById(R.id.ttRoundsText)

        resetButton.visibility = View.GONE
        resetButton.isEnabled = false

        // Start/Stop Button
        startStopButton.setOnClickListener {
            if (!isRunning) {
                startTimer()
            } else {
                stopTimer()
            }
        }

        // Reset Button
        resetButton.setOnClickListener {
            resetTimer()
        }
    }

    private fun startTimer() {
        // Get input values
        roundTime = roundTimeInput.text.toString().toLongOrNull()?.times(1000) ?: 0L
        breakTime = breakTimeInput.text.toString().toLongOrNull()?.times(1000) ?: 0L
        totalRounds = roundsInput.text.toString().toIntOrNull() ?: 0
        currentRound = 0
        isRunning = true

        type.text = "Fight"
        roundCounter.visibility = View.VISIBLE
        type.visibility = View.VISIBLE
        resetButton.visibility = View.VISIBLE
        resetButton.isEnabled = true
        roundTimeInput.visibility = View.GONE
        breakTimeInput.visibility = View.GONE
        roundsInput.visibility = View.GONE

        roundTime2.text = roundTimeInput.text
        breakTime2.text = breakTimeInput.text
        rounds2.text = roundsInput.text
        roundTime2Text.visibility = View.VISIBLE
        breakTime2Text.visibility = View.VISIBLE
        rounds2Text.visibility = View.VISIBLE

        startStopButton.animate()
            .translationY(280f)
            .setDuration(500)
            .start();

        getReady()
    }

    private fun runNextPhase() {
        getReady.visibility = View.GONE
        val timeToUse = if (isRoundPhase) roundTime else breakTime
        timer = object : CountDownTimer(timeToUse, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                timerText.text = String.format("%02d:%02d", TimeUnit.SECONDS.toMinutes(secondsLeft), secondsLeft % 60)
                val progress = ((millisUntilFinished.toFloat() / timeToUse) * 100).toInt()
                progressCircle.progress = progress

                var reallyCurrentRound = currentRound + 1
                roundCounter.text = "$reallyCurrentRound/$totalRounds"
            }

            override fun onFinish() {
                playSound()
                if (isRoundPhase) {
                    isRoundPhase = false
                    type.text = "Break"
                } else {
                    currentRound++
                    isRoundPhase = true
                    type.text = "Fight"
                }

                if (currentRound < totalRounds) {
                    runNextPhase()
                } else {
                    resetTimer()
                }
            }
        }
        timer?.start()
    }

    private fun stopTimer() {
        timer?.cancel()
        isRunning = false
    }

    private fun resetTimer() {
        timer?.cancel()
        progressCircle.progress = 100
        timerText.text = "00:00"
        isRunning = false
        isRoundPhase = true
        currentRound = 0
        resetButton.visibility = View.GONE
        resetButton.isEnabled = false
        roundCounter.visibility = View.GONE
        type.visibility = View.GONE

        roundTimeInput.visibility = View.VISIBLE
        breakTimeInput.visibility = View.VISIBLE
        roundsInput.visibility = View.VISIBLE

        roundTime2.text = ""
        breakTime2.text = ""
        rounds2.text = ""
        roundTime2Text.visibility = View.GONE
        breakTime2Text.visibility = View.GONE
        rounds2Text.visibility = View.GONE

        startStopButton.animate()
            .translationY(0f)
            .setDuration(500)
            .start();
    }

    private fun getReady() {
        getReady.visibility = View.VISIBLE
        val totalTime = 4000L // 3..2..1..Go! (4 steps)
        val interval = 1000L  // Every second

        object : CountDownTimer(totalTime, interval) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = (millisUntilFinished / 1000).toInt()
                getReady.text = when (secondsLeft) {
                    3 -> "3"
                    2 -> "2"
                    1 -> "1"
                    else -> ""
                }
            }

            override fun onFinish() {
                playSound()
                runNextPhase()
            }
        }.start()
    }

    private fun playSound() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.boxing_bell)
        mediaPlayer.start()
    }
}

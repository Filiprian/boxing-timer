package com.example.boxingtimer

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
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

        startStopButton.text = "Stop"
        runNextPhase()
    }

    private fun runNextPhase() {
        val timeToUse = if (isRoundPhase) roundTime else breakTime
        timer = object : CountDownTimer(timeToUse, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                timerText.text = String.format("%02d:%02d", TimeUnit.SECONDS.toMinutes(secondsLeft), secondsLeft % 60)
                val progress = ((millisUntilFinished.toFloat() / timeToUse) * 100).toInt()
                progressCircle.progress = progress

                var reallyCurrentRound = currentRound + 1
                roundCounter.text = "$reallyCurrentRound/$totalRounds rounds"
            }

            override fun onFinish() {
                playSound()
                if (isRoundPhase) {
                    isRoundPhase = false
                } else {
                    currentRound++
                    isRoundPhase = true
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
        startStopButton.text = getString(R.string.start)
    }

    private fun resetTimer() {
        timer?.cancel()
        progressCircle.progress = 100
        timerText.text = "00:00"
        isRunning = false
        isRoundPhase = true
        currentRound = 0
        startStopButton.text = getString(R.string.start)
    }

    private fun playSound() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.boxing_bell)
        mediaPlayer.start()
    }
}

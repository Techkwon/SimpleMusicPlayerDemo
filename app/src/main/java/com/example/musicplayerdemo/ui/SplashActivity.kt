package com.example.musicplayerdemo.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.musicplayerdemo.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class SplashActivity : AppCompatActivity() {

    companion object {
        private const val SPLASH_TIME = 2000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Glide.with(this).load(R.drawable.splash_flo).into(this.iv_splash)
        waitDisplay()
    }

    private fun waitDisplay() = launchWork {
        delay(SPLASH_TIME)
        goToPlayer()
        return@launchWork
    }

    private fun goToPlayer() {
        val intent = Intent(this, PlayerActivity::class.java)
        startActivity(intent)
        finish()
    }

    private inline fun launchWork(crossinline block: suspend () -> Unit): Job {
        return CoroutineScope(Dispatchers.Default + Job()).launch {
            block()
        }
    }
}

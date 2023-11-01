package com.example.template_flo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.template_flo.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val handler = Handler(Looper.getMainLooper())

        handler.postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
        }, 1000)
    }
}
package com.example.unacademyassignment

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etBlobAnimationPercentage = findViewById<AppCompatEditText>(R.id.etBlobAnimationPercentage)
        val btnAnimate = findViewById<AppCompatButton>(R.id.btnAnimate)
        val progressBar = findViewById<View>(R.id.progressBar) as CircularProgressBar

        btnAnimate.setOnClickListener { progressBar.setProgress(etBlobAnimationPercentage.text.toString().toInt()) }
    }
}

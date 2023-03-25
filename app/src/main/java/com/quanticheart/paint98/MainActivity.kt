package com.quanticheart.paint98

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.quanticheart.paint98.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startActivity(Intent(this, com.quanticheart.paint.MainActivity::class.java))
        finish()
    }
}
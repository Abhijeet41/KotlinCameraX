package com.abhi41.cameraappkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.abhi41.cameraappkotlin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCamerax.setOnClickListener {
            val intent = Intent(this, CameraxActivity::class.java)
            startActivity(intent)
        }

        binding.btnNativeCamera.setOnClickListener {
            val intent = Intent(this, NativeCamera::class.java)
            startActivity(intent)
        }
    }
}
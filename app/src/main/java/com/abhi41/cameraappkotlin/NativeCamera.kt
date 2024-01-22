package com.abhi41.cameraappkotlin

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.abhi41.cameraappkotlin.databinding.ActivityNativeCameraBinding
import java.io.File

class NativeCamera : AppCompatActivity() {
    lateinit var binding: ActivityNativeCameraBinding
    private lateinit var imageUri: Uri

    private val capturedImg = registerForActivityResult(ActivityResultContracts.TakePicture()){
        binding.imgPreview.setImageURI(null)
        //binding.imgPreview.setImageURI(imageUri)

        val bitmap = contentResolver.openInputStream(imageUri).use { data ->
            BitmapFactory.decodeStream(data)
        }
        binding.imgPreview.setImageBitmap(bitmap)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNativeCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageUri = createImageUri()

        binding.btnCaptureImg.setOnClickListener {
            capturedImg.launch(imageUri)
        }

    }

    private fun createImageUri(): Uri{
        val image = File(filesDir,"camera_photos.png")
        return FileProvider.getUriForFile(applicationContext,"${BuildConfig.APPLICATION_ID}.fileprovider",image)
    }
}
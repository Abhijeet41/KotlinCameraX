package com.abhi41.cameraappkotlin

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.abhi41.cameraappkotlin.databinding.ActivityCameraxBinding
import java.io.File
import java.util.concurrent.Executors


class CameraxActivity : AppCompatActivity() {
    lateinit var binding: ActivityCameraxBinding
    var cameraFacing = CameraSelector.LENS_FACING_BACK

    private val activityResultLauncher = registerForActivityResult<String, Boolean>(
        ActivityResultContracts.RequestPermission()
    ) { result ->
        if (result) {
            startCamera(cameraFacing)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraxBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            activityResultLauncher.launch(Manifest.permission.CAMERA)
        } else {
            startCamera(cameraFacing)
        }

        binding.apply {
            imgFlipCamera.setOnClickListener {
                if (cameraFacing == CameraSelector.LENS_FACING_BACK) {
                    cameraFacing = CameraSelector.LENS_FACING_FRONT
                } else {
                    cameraFacing = CameraSelector.LENS_FACING_BACK
                }
                startCamera(cameraFacing)
            }
        }
    }

    private fun startCamera(cameraFacing: Int) {
        try {
            var aspectRatio: Int = aspectRatio(binding.imgPreview.width, binding.imgPreview.height)
            var listenableFuture = ProcessCameraProvider.getInstance(applicationContext)

            listenableFuture.addListener({
                val cameraProvider = listenableFuture.get()
                val preview = Preview.Builder()
                    .setTargetAspectRatio(aspectRatio)
                    .build()
                val imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .setTargetRotation(windowManager.defaultDisplay.rotation)
                    .build()

                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(cameraFacing)
                    .build()

                cameraProvider.unbindAll()

                val camera =
                    cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
                binding.imgCapture.setOnClickListener {
                    if (ContextCompat.checkSelfPermission(
                            applicationContext,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        takePicture(imageCapture)
                    }
                }

                binding.toggleFlash.setOnClickListener {
                    setFlashIcon(camera)
                }
                preview.setSurfaceProvider(binding.imgPreview.surfaceProvider)
            }, ContextCompat.getMainExecutor(applicationContext))
        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun setFlashIcon(camera: Camera) {
        if (camera.cameraInfo.hasFlashUnit()) {
            if (camera.cameraInfo.torchState.value == 0) {
                camera.cameraControl.enableTorch(true)
                binding.toggleFlash.setImageResource(R.drawable.icon_flash_off_24)
            } else {
                camera.cameraControl.enableTorch(true)
                binding.toggleFlash.setImageResource(R.drawable.icon_flash_on_24)
            }
        } else {
            runOnUiThread {
                Toast.makeText(applicationContext, "Flash is not support", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    private fun takePicture(imageCapture: ImageCapture) {
        val file = File(getExternalFilesDir(null), System.currentTimeMillis().toString() + ".jpg")
        //val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES+"/Camerax"), System.currentTimeMillis().toString() + ".jpg")
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        imageCapture.takePicture(
            outputFileOptions,
            Executors.newCachedThreadPool(),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    runOnUiThread {
                        Toast.makeText(
                            applicationContext,
                            "Image saved at: " + file.path,
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("onImageSaved: ",file.path)
                    }
                    startCamera(cameraFacing)
                }

                override fun onError(exception: ImageCaptureException) {
                    runOnUiThread {
                        Toast.makeText(
                            applicationContext,
                            "Failed to save: " + exception.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    startCamera(cameraFacing)
                }
            })

    }

    private fun aspectRatio(width: Int, height: Int): Int {
        var previewRatio = Math.max(width, height) / Math.min(width, height)
        if (Math.abs(previewRatio - 4.0 / 3.0) <= Math.abs(previewRatio - 16.0 / 9.0)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

}
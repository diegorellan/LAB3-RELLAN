package com.example.lab3_rellan

import android.app.Activity
import android.content.Context.CAMERA_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.SparseIntArray
import android.view.Surface
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import com.google.mlkit.vision.objects.defaults.PredefinedCategory
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {
    val CAMERA_REQUEST_CODE=0
    val pickPhotoRequestCode=150
    //Here we detect the buttons and the imageView
    val camera_button=findViewById<android.widget.Button>(R.id.camera_button)
    val gallery_button=findViewById<android.widget.Button>(R.id.gallery_button)
    val picture_shown=findViewById<android.widget.ImageView>(R.id.camera_picture)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //We set up the camera working

        //Now we add an onClickListener for the buttons
        camera_button.setOnClickListener {
            val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (callCameraIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(callCameraIntent, CAMERA_REQUEST_CODE)
            }

        }

        gallery_button.setOnClickListener {
            pickImage()
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    picture_shown.setImageBitmap(data.getExtras()?.get("data") as Bitmap)
                    val bitmap = data.getExtras()?.get("data") as Bitmap
                    val image = InputImage.fromBitmap(bitmap, 0)
                    //Image processing
                    configureAndRunImageLabeler(image)
                    //Text detection
                    recognizeText(image)
                    //Object recognizion
                    detectObjects(image)
                }
            }
            else -> {

            }
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, pickPhotoRequestCode)
    }

    //Here we define the function that labels the image
    private fun configureAndRunImageLabeler(image: InputImage) {
        val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
        labeler.process(image).addOnSuccessListener { labels ->
            for (label in labels) {
                val text = label.text
                val confidence = label.confidence
                val index = label.index
            }
        }
            .addOnFailureListener { e ->
                // Task failed with an exception
            }
    }

    //Here we define the function that detects and recognize text
    private fun recognizeText(image: InputImage) {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val result = recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val resultText = visionText.text
                for (block in visionText.textBlocks) {
                    val blockText = block.text
                    val blockCornerPoints = block.cornerPoints
                    val blockFrame = block.boundingBox
                    for (line in block.lines) {
                        val lineText = line.text
                        val lineCornerPoints = line.cornerPoints
                        val lineFrame = line.boundingBox
                        for (element in line.elements) {
                            val elementText = element.text
                            val elementCornerPoints = element.cornerPoints
                            val elementFrame = element.boundingBox
                        }
                    }
                }

            }
            .addOnFailureListener { e ->

            }

    }

    //Here we define the function that detects objects
    private fun detectObjects(image: InputImage){
        val options = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableMultipleObjects()
            .enableClassification()  // Optional
            .build()
        val objectDetector = ObjectDetection.getClient(options)
        objectDetector.process(image)
            .addOnSuccessListener { detectedObjects ->
                for (detectedObject in detectedObjects) {
                    val boundingBox = detectedObject.boundingBox
                    val trackingId = detectedObject.trackingId
                    for (label in detectedObject.labels) {
                        val text = label.text
                        val index = label.index
                        val confidence = label.confidence
                    }
                }
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
            }
    }



}


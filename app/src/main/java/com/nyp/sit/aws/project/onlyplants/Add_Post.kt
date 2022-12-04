package com.nyp.sit.aws.project.onlyplants

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.ImageView
import aws.smithy.kotlin.runtime.util.asyncLazy
import com.nyp.sit.aws.project.onlyplants.Rekognition.detectModLabels
import kotlinx.coroutines.GlobalScope
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class Add_Post : AppCompatActivity() {
    private lateinit var imageView: ImageView
    var imageuploaded = false

    companion object{
        val Image_Request_Code=100
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)
        imageView = findViewById<ImageView>(R.id.picture_to_be_posted)

        imageView.setOnClickListener{
            pickImageFromGallery()
        }
    }
    private fun pickImageFromGallery(){
        val intent =    Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, Image_Request_Code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && !imageuploaded) {
            val selectedimage = data?.data
            imageView.setImageURI(data?.data)
            if (selectedimage != null) {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedimage)
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()
                val encodedString:   ByteArray = Base64.encode(byteArray, Base64.DEFAULT)
//                GlobalScope.launch { detectModLabels(encodedString) }
//                runBlocking {
//                    launch { detectModLabels(encodedString) }
//                }
            }
            imageuploaded = true
        } else {
            imageuploaded = false
        }
    }
}
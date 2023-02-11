package com.nyp.sit.aws.project.onlyplants.View.PlantIdentifier

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.nyp.sit.aws.project.onlyplants.Model.Plant.PlantService
import com.nyp.sit.aws.project.onlyplants.R
import com.nyp.sit.aws.project.onlyplants.View.PlantInformation.PlantInformationActivity
import kotlinx.android.synthetic.main.activity_plant_identifier.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class PlantIdentifierActivity : AppCompatActivity() {
    lateinit var imageView : ImageView
    lateinit var button : Button
    private val pickImage = 100
    private var encodedImage : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plant_identifier)

        imageView = findViewById(R.id.pid_PlantImage)
        button = findViewById(R.id.pid_B_SelectPhoto)

        button.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }


        pid_B_DetectPlant.setOnClickListener {
            if (encodedImage != null) {
                GlobalScope.launch {
                    val result = "Food"
                    //val result = PlantService().GetPlantType(encodedImage!!)

                    val plantInfoIntent = Intent(this@PlantIdentifierActivity, PlantInformationActivity::class.java)
                    plantInfoIntent.putExtra("plant_image", encodedImage!!.toByteArray())
                    plantInfoIntent.putExtra("plant_name", result)
                    this@PlantIdentifierActivity.startActivity(plantInfoIntent)
                }
            } else {
                Toast.makeText(this@PlantIdentifierActivity, "Please select a photo", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == pickImage) {
            val imageUri = data?.data
            imageView.setImageURI(imageUri)
            val imageStream = contentResolver.openInputStream(imageUri!!)

            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = false
            options.inSampleSize = 2

            val selectedImage = BitmapFactory.decodeStream(imageStream, null, options)
            encodedImage = encodeImage(selectedImage!!)
        }
    }

    fun encodeImage(bm : Bitmap) : String{
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()

        val encImage = Base64.encodeToString(b, Base64.DEFAULT)

        return encImage
    }
}
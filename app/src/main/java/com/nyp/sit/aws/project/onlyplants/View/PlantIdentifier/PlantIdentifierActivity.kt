package com.nyp.sit.aws.project.onlyplants.View.PlantIdentifier

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.*
import com.nyp.sit.aws.project.onlyplants.MainActivity
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

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_home)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        imageView = findViewById(R.id.pid_PlantImage)
        button = findViewById(R.id.pid_B_SelectPhoto)

        button.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }


        pid_B_DetectPlant.setOnClickListener {
            if (encodedImage != null) {
                GlobalScope.launch {
                    //val result = "Food"
                    val result = PlantService().GetPlantType(encodedImage!!)

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
            options.inSampleSize = 3

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.translateBtn -> {
                val dialog = Dialog(this)
                dialog.setContentView(R.layout.popup_translate)
                val spinner1 = dialog.findViewById<Spinner>(R.id.pu_FromLang)
                val spinner2 = dialog.findViewById<Spinner>(R.id.pu_ToLang)
                val button = dialog.findViewById<Button>(R.id.pu_Button_Translate)

                val spinnerFromLang = dialog.findViewById<Spinner>(R.id.pu_FromLang)
                val itemsFromLang = arrayOf("en", "fr", "zh")
                val fromAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, itemsFromLang)

                spinner1.adapter = fromAdapter
                spinner2.adapter = fromAdapter

                dialog.show()

                button.setOnClickListener {

                    GlobalScope.launch {
                        dialog.dismiss()

                        val fromLang = spinner1.selectedItem.toString()
                        val toLang = spinner2.selectedItem.toString()

                        val rootView = findViewById<ViewGroup>(android.R.id.content)

                        PlantService().translateViews(rootView, fromLang, toLang)
                    }

                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.translate_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
package com.nyp.sit.aws.project.onlyplants.View.PlantInformation

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.nyp.sit.aws.project.onlyplants.AddPost
import com.nyp.sit.aws.project.onlyplants.Model.LanguageTranslate.LanguageTranslateService
import com.nyp.sit.aws.project.onlyplants.Model.Plant.PlantService
import com.nyp.sit.aws.project.onlyplants.R
import kotlinx.android.synthetic.main.activity_language_translate.*
import kotlinx.android.synthetic.main.activity_plant_information.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlantInformationActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plant_information)

        if (intent != null)  {
            val encodedPlantImg = intent.getByteArrayExtra("plant_image")?.let {
                String(it)
            }
            val imageBytes = Base64.decode(encodedPlantImg,Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

            val plantName = intent.getStringExtra("plant_name")

            GlobalScope.launch {
                val result = PlantService().GetPlantInformation(plantName!!)

                runOnUiThread{
                    pin_IV_PlantImage.setImageBitmap(bitmap)
                    pin_TV_PlantName.text = "Plant Type: $plantName"
                    pin_TV_PlantDescription.text = result
                }
            }

        }

        pin_Button_Translate.setOnClickListener {

            if (intent != null) {
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

                        translateViews(rootView, fromLang, toLang)
                    }

                }
            }
        }

        pin_Button_Post.setOnClickListener {
            val intent = Intent(this, AddPost::class.java)
            startActivity(intent)
        }

        pin_Button_SearchMore.setOnClickListener {
            val plantSearchIntent = Intent(this, PlantSearch::class.java)
            startActivity(plantSearchIntent)
        }

    }

    suspend fun translateViews(view: View, fromLang: String, toLang :String) {

        if (view is TextView) {

            if ((view !is Spinner)) {
                val result = withContext(Dispatchers.IO) {
                    LanguageTranslateService().GetTranslatedText(fromLang, toLang, view.text.toString())
                }

                runOnUiThread{
                    view.text = result
                }
            }

        } else if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                translateViews(view.getChildAt(i), fromLang, toLang)
            }
        }
    }

}
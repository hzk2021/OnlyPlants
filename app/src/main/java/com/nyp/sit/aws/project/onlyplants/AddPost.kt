package com.nyp.sit.aws.project.onlyplants

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.Image
import android.net.Uri
import java.io.ByteArrayOutputStream
import android.util.Base64;
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import com.nyp.sit.aws.project.onlyplants.Model.Social.SocialMediaService
import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.coroutines.*
import okhttp3.Dispatcher
import kotlinx.android.synthetic.main.activity_add_post.*

class AddPost : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var buttonadd:ImageButton
    private lateinit var captiontext:TextView
    private lateinit var closebutton : ImageButton
    private lateinit var toolbar : Toolbar
    var pictureexist=false
    var moderationresult=""
    var base64Image=""
    companion object{
        val Image_Request_Code=100
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)


        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_home)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        imageView = findViewById<ImageView>(R.id.picture_to_be_posted)
        buttonadd=findViewById<ImageButton>(R.id.post_picture)
        captiontext=findViewById<TextView>(R.id.write_caption)
        closebutton=findViewById(R.id.dont_post_picture)
        val overlay = findViewById<View>(R.id.overlay)
        imageView.setOnClickListener{
            pickImageFromGallery()
        }
        closebutton.setOnClickListener{
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }
        buttonadd.setOnClickListener {
            if((captiontext.text.toString().trim()!="") and pictureexist) {
                closebutton.isEnabled=false
                buttonadd.isEnabled=false
                imageView.isEnabled=false
                overlay.setBackgroundColor(Color.GRAY)
                overlay.background.alpha=200
                overlay.visibility=View.VISIBLE
                GlobalScope.launch(Dispatchers.IO) {
                    val imageurl = SocialMediaService().UploadImageToS3(base64Image)
                    if (imageurl.trim() != "") {
                        SocialMediaService().CreatePost(
                            caption = captiontext.text.toString(),
                            imageUrl = imageurl.replace("\"", "")
                        )
                        val intent = Intent(this@AddPost,Home::class.java)
                        startActivity(intent)
                    }
                }
            }
            else {
                showToast("No picture selected/Empty caption")
            }
        }

    }
    private fun pickImageFromGallery(){
        val intent =    Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, Image_Request_Code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val selectedimage = data?.data
        pictureexist=false
        if (requestCode == 100 && resultCode == RESULT_OK) {
            if (selectedimage != null) {
                val imageUri: Uri = selectedimage
                val imageStream = contentResolver.openInputStream(imageUri)
                val selectedImage = BitmapFactory.decodeStream(imageStream)
                val byteArrayOutputStream = ByteArrayOutputStream()
                val fileExtension = MimeTypeMap.getFileExtensionFromUrl(imageUri.toString())
                val compressFormat = when(fileExtension) {
                    "jpeg" -> Bitmap.CompressFormat.JPEG
                    "png" -> Bitmap.CompressFormat.PNG
                    else -> Bitmap.CompressFormat.JPEG
                }
                selectedImage.compress(compressFormat, 100, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()
                base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT)
                GlobalScope.launch(Dispatchers.IO){
                    moderationresult=SocialMediaService().DetectModerate(base64Image)
                    if(moderationresult=="true") {
                        pictureexist=true
                        runOnUiThread {
                            imageView.setImageURI(selectedimage)
                            showToast("Picture Added!")
                        }

                    } else{
                        pictureexist=false
                        runOnUiThread{
                            imageView.setImageResource(R.drawable.add_image_icon)
                            showToast("Picture Contains Moderated Content.\nTry Again Without adding pictures that are not allowed!")
                        }
                    }
                }

            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun showToast(message: String) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_LONG)
        toast.show()
    }

}
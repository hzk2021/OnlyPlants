package com.nyp.sit.aws.project.onlyplants.Model.Social

import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


class SocialMediaService {

    private val client = OkHttpClient()
    private val protocol = "https://"
    private val domain = "xngw8jryn2.execute-api.us-east-1.amazonaws.com"
    private val getallpostPath = "/post/GetAllPost"
    private val createPostPath = "/post/PostOnePost"
    private val checkmoderationPath="/post/DetectModerate"
    private val uploadimagePath="/post/imagetos3bucket"

    suspend fun GetAllPost() : String{
            val uriPath = "$protocol$domain$getallpostPath"
            val request = Request.Builder().url(uriPath).build()

            var Getallpost : String

            Log.d("Getting plant post from", uriPath)

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                    Getallpost=response.body.string()
                    Log.d("GetAllPostReply" ,"Retrieve Success")
                } else {
                    Log.d("PostRetrievalError", response.message)
                    Getallpost = "Retrieved Error"
                }

                return Getallpost
            }
    suspend fun CreatePost(caption : String , imageUrl :String) : String{
        val uriPath = "$protocol$domain$createPostPath"

        val jsonObject = JSONObject()
        jsonObject.put("Caption", caption)
        jsonObject.put("Url", imageUrl)

        Log.d("Result",jsonObject.toString())
        val body: RequestBody = jsonObject.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder().url(uriPath)
            .post(body).build()

        var CreatePost : String = ""
        Log.d("Adding Plant Post to database", uriPath)
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            CreatePost=response.body.string()
            Log.d("Result" ,"Create Success")
        } else {
            Log.d("PostRetrievalError", response.message)
            CreatePost ="Error"
        }
        return CreatePost
    }
    suspend fun DetectModerate(base64image:String) : String{
        val uriPath = "$protocol$domain$checkmoderationPath"

        val jsonObject = JSONObject()
        jsonObject.put("base64_image", base64image)

        val body: RequestBody = jsonObject.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder().url(uriPath)
            .post(body).build()

        var detectmoderation : String = ""
        Log.d("Checking For Moderated Content", uriPath)
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            detectmoderation=response.body.string()
            Log.d("Result", detectmoderation)
        } else {
            Log.d("PostRetrievalError", response.message)
            detectmoderation ="Error"
        }
        return detectmoderation
    }
    suspend fun UploadImageToS3(base64image:String) : String{
        val uriPath = "$protocol$domain$uploadimagePath"

        val jsonObject = JSONObject()
        jsonObject.put("image_data", base64image)

        val body: RequestBody = jsonObject.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder().url(uriPath)
            .post(body).build()

        var uploadimagetos3 : String = ""
        Log.d("Uploading Image To S3", uriPath)
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            uploadimagetos3=response.body.string()
            Log.d("Result", uploadimagetos3)
        } else {
            Log.d("PostRetrievalError", response.message)
            uploadimagetos3 ="Error"
        }
        return uploadimagetos3
    }
    }


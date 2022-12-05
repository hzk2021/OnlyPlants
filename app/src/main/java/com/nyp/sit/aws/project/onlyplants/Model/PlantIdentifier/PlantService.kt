package com.nyp.sit.aws.project.onlyplants.Model.PlantIdentifier

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import okhttp3.*
import okio.IOException
import org.json.JSONObject

class PlantService {

    private val client = OkHttpClient()
    private val protocol = "https://"
    private val domain = "l2suv669dc.execute-api.us-east-1.amazonaws.com"
    private val getPlantInfoPath = "/dev/plants/information"
    private val getPlantTypePath = "/dev/plants/type"
    private val getTranslationPath = "/translate"
    //private val parameters = "?action=query&format=json&list=search&utf8=1&formatversion=2&srsearch="
    //private val postfix = "%20plant"


    /** Currently using mock API endpoints, will change later **/
    suspend fun GetPlantInformation(plant_name : String) : String {

        val uriPath = "$protocol$domain$getPlantInfoPath?name=$plant_name"
        val request = Request.Builder().url(uriPath).build()

        var plantInfo = ""

        Log.d("Getting plant information from", uriPath)

        val response = client.newCall(request).execute()

        if (response.isSuccessful) {
            plantInfo = JSONObject(response.body.string()).getString("message").toString()
            Log.d("PlantInfoReply", plantInfo)
        } else {
            Log.d("PlantInfoRetrievalError", response.message)
            plantInfo = "RetrievalError"
        }

        return plantInfo
    }

    suspend fun GetPlantType(plant_image_in_bytes : ByteArray) : String {

        val uriPath = "$protocol$domain$getPlantTypePath?image_bytes=$plant_image_in_bytes"
        val request = Request.Builder().url(uriPath).build()

        var plantType = ""

        Log.d("Getting plant type from", uriPath)

        val response = client.newCall(request).execute()

        if (response.isSuccessful) {
            plantType = JSONObject(response.body.string()).getString("message").toString()
            Log.d("PlantTypeReply", plantType)
        } else {
            Log.d("PlantTypeRetrievalError", response.message)
            plantType = "RetrievalError"
        }

        return plantType

    }
}
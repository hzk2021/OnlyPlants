package com.nyp.sit.aws.project.onlyplants.Model.Plant

import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class PlantService : IPlantService {

    private val client = OkHttpClient()
    private val protocol = "https://"
    private val domain = "kbqsvtu5gd.execute-api.us-east-1.amazonaws.com"
    private val getPlantInfoPath = "/prod/plant/information"
    private val getPlantTypePath = "/prod/plant/type"
    //private val parameters = "?action=query&format=json&list=search&utf8=1&formatversion=2&srsearch="
    //private val postfix = "%20plant"


    override suspend fun GetPlantInformation(plant_name : String) : String {

        val uriPath = "$protocol$domain$getPlantInfoPath?name=$plant_name"
        val request = Request.Builder().url(uriPath).build()

        var plantInfo = ""

        Log.d("Getting plant information from", uriPath)

        val response = client.newCall(request).execute()

        if (response.isSuccessful) {
            plantInfo = response.body!!.string().replace("\"", "").replace("\\n"," ")
            Log.d("PlantInfoReply", plantInfo)
        } else {
            Log.d("PlantInfoRetrievalError", response.message)
            plantInfo = "RetrievalError"
        }

        return plantInfo
    }

    override suspend fun GetPlantType(base_64_image : String) : String {

        val uriPath = "$protocol$domain$getPlantTypePath"

        Log.d("Getting plant type from", uriPath)

        val body : RequestBody = base_64_image.toRequestBody("application/json".toMediaType())

        val request = Request.Builder().url(uriPath).post(body).build()

        var plantType = ""

        val response = client.newCall(request).execute()

        if (response.isSuccessful) {
            plantType = response.body!!.string()
            Log.d("PlantTypeReply", plantType)
        } else {
            plantType = response.body!!.string()

            if (plantType.contains("Internal server error")) {
                plantType = "Not a plant"
            }
            Log.d("PlantTypeRetrievalError", plantType)
        }

        return plantType

    }
}
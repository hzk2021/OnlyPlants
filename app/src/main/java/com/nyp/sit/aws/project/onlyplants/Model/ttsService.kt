package com.nyp.sit.aws.project.onlyplants.Model

import java.util.Base64.*
import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject



class ttsService {

    // Initialize client and url paths
    private val client = OkHttpClient()
    private val protocol = "https://"
    private val ttsDomain = "0h3qii72ok.execute-api.us-east-1.amazonaws.com"
    private val getTTSPath = "/test/converttoaudio"

    private val raDomain = "trom13phtk.execute-api.us-east-1.amazonaws.com"
    private val getRaPath = "/test/retrieveaudio"

    fun retrieveAudio(flower : String): ByteArray? {

        // Put parameters into JSON
        val json = JSONObject()
        json.put("flower", flower)

        val body = json.toString().toRequestBody(("application/json").toMediaType())

        // Build request path
        val uriPath = "$protocol$raDomain$getRaPath"
        val request = Request.Builder()
            .url(uriPath)
            .post(body)
            .build()

        Log.d("Retrieving audio from", uriPath)

        var decodedBody: ByteArray? = null

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                Log.d("responseMsg", "retrieve successful")

                val resp = response.body!!.string()

                // Sometimes error but returned as success (status code 200)
                println("response: $resp")

                // Decode response
                decodedBody = getDecoder().decode(resp)
            }
            else {
                Log.d("responseMsg", "retrieve failed")
                Log.d("responsefullMsg", response.code.toString())
                Log.d("responsefullMsg", response.body.toString())
            }
        } catch (e: Exception) {
            Log.d("responseMsg", "code failed")
            e.printStackTrace()
        }

        return decodedBody
    }

    // Function to convert text to audio
    fun convertTTS(text : String, flower : String) {

        // Put parameters into JSON
        val json = JSONObject()
        json.put("text", text)
        json.put("flower", flower)

        val body = json.toString().toRequestBody(("application/json").toMediaType())

        // Build request path
        val uriPath = "$protocol$ttsDomain$getTTSPath"
        val request = Request.Builder()
            .url(uriPath)
            .post(body)
            .build()


        Log.d("Converting text to audio at", uriPath)

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                Log.d("responseMsg", "conversion successful")
            }
            else {
                Log.d("responseMsg", "conversion failed")
                Log.d("responsefullMsg", response.code.toString())
                Log.d("responsefullMsg", response.body.toString())
            }
        } catch (e: Exception) {
            Log.d("responseMsg", "code failed")
            e.printStackTrace()
        }

        return
    }
}
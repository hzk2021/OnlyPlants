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
    private val ttsDomain = "zo7awrj5hb.execute-api.us-east-1.amazonaws.com"
    private val getTTSPath = "/test/converttoaudio"

    private val raDomain = "z5h0vp4o4g.execute-api.us-east-1.amazonaws.com"
    private val getRaPath = "/test/retrieveaudio"

    // Function to convert text to audio
    fun ConvertTTS( text : String, voice : String, flower : String) {

        // Put parameters into JSON
        val json = JSONObject()
        json.put("text", text)
        json.put("voice", voice)
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
                Log.d("responsefullMsg", response.body.string())
            }
        } catch (e: Exception) {
            Log.d("responseMsg", "code failed")
            e.printStackTrace()
        }

        return
    }

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

                // Decode response
                decodedBody = getDecoder().decode(response.body.string())
            }
            else {
                Log.d("responseMsg", "retrieve failed")
                Log.d("responsefullMsg", response.code.toString())
                Log.d("responsefullMsg", response.body.string())
            }
        } catch (e: Exception) {
            Log.d("responseMsg", "code failed")
            e.printStackTrace()
        }

        return decodedBody
    }

}

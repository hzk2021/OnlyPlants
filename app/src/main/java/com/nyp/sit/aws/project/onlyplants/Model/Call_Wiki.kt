package com.nyp.sit.aws.project.onlyplants.Model
import android.util.Log
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONObject

import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL


class Call_Wiki {
    private val client = OkHttpClient()
    private val protocol = "https://"
    private val domain = "vmo0ymtfn1.execute-api.us-east-1.amazonaws.com"
    private val getPlantInfoPath = "/dev/plants/information"
    private val getPlantTypePath = "/dev/plants/type"
    private val getTranslationPath = "/translate"
    //private val parameters = "?action=query&format=json&list=search&utf8=1&formatversion=2&srsearch="
    //private val postfix = "%20plant"


    /** Currently using mock API endpoints, will change later **/

    //suspend fun GetWikiInformation(wiki_name : String) : Deferred<String> = CoroutineScope(IO).async{
    suspend fun GetWikiInformation() : String{
        //val uriPath = "$protocol$domain$getPlantInfoPath?name=$plant_name"
        //val uriPath = "https://p8upla1755.execute-api.us-east-1.amazonaws.com/prod/testing"
        val uriPath = "https://0vquhvi392.execute-api.ap-southeast-1.amazonaws.com/prod"
        val request = Request.Builder().url(uriPath).build()

        var GetWikiInfo = ""

        Log.d("Getting Wiki information from", uriPath)

        val response = client.newCall(request).execute()
        if (response.isSuccessful){
            GetWikiInfo = response.body!!.string()
            Log.d("WikiInfoReply", GetWikiInfo)
        }
        else{
            Log.d("WikiInfoRetrievalError",response.message)
            GetWikiInfo = "Retreieved Error"
        }

        return GetWikiInfo
    }
    fun postWikiSearch(search:String): String {
        val url = URL("https://0vquhvi392.execute-api.ap-southeast-1.amazonaws.com/prod/post")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.doOutput = true

        val json = JSONObject()
        json.put("input",search)
//        val test = json.getString("body")
//        Log.d("test log",test)
        val body = json.toString().toByteArray(Charsets.UTF_8)
        conn.setRequestProperty("Content-Length", body.size.toString())
        conn.setRequestProperty("Content-Type", "application/json")
        conn.outputStream.write(body)
        conn.connect()

        val responseCode = conn.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val responseData = conn.inputStream.bufferedReader().use(BufferedReader::readText)
            val jsonResponse = JSONObject(responseData)
            val respBody = jsonResponse.getString("body")
            Log.d("Post Success", respBody)

            if (respBody == "...") {
                return "No results found for: $search"
            }
            else {
                // Convert text to audio
                convertTTS(respBody, search)

                return respBody
            }
        } else {
             Log.d("Error", responseCode.toString())
            return responseCode.toString()
        }

    }

    // Function to convert text to audio
    private fun convertTTS(text: String, flower: String) {
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        val singleJobItem =
            scope.async(Dispatchers.IO) {
                ttsService().convertTTS(text, flower)
            }

        scope.launch { singleJobItem.await() }
    }

//    suspend fun GetPlantType(plant_image_in_bytes : ByteArray) : Deferred<String> = CoroutineScope(IO).async {
//
//        val uriPath = "$protocol$domain$getPlantTypePath?image_bytes=$plant_image_in_bytes"
//        val request = Request.Builder().url(uriPath).build()
//
//        var plantType = ""
//
//        Log.d("Getting plant type from", uriPath)
//
//        client.newCall(request).enqueue(object : Callback {
//            override fun onResponse(call: Call, response: Response) {
//                plantType = JSONObject(response.body.string()).getString("message").toString()
//                Log.d("PlantTypeReply", response.body.string())
//            }
//
//            override fun onFailure(call: Call, e: IOException) {
//                //TODO("Not yet implemented")
//                Log.d("PlantTypeRetrievalError", e.toString())
//            }
//        })
//
//        return@async plantType
//    }
}
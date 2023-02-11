package com.nyp.sit.aws.project.onlyplants.Model

import android.provider.Settings.Global.getString
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.nyp.sit.aws.project.onlyplants.ReminderFormActivity
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.buildJsonArray
import org.json.JSONArray

class reminderService {

    // Initialize client and url paths
    private val client = OkHttpClient()
    private val protocol = "https://"
    private val createReminderDomain = "d2i8w9vs9g.execute-api.us-east-1.amazonaws.com"
    private val createReminderPath = "/test/createreminderevent"

    private val getReminderDomain = "nmwcsprxwk.execute-api.us-east-1.amazonaws.com"
    private val getReminderPath = "/test/getreminders"

    // Function to create Eventbridge rule
    fun createReminderRule(cronExp: String, deviceToken: String) {

        val json = JSONObject()
        json.put("cronExp", cronExp)
        json.put("deviceToken", deviceToken)

        val body = json.toString().toRequestBody(("application/json").toMediaType())

        // Build request path
        val uriPath = "$protocol$createReminderDomain$createReminderPath"
        val request = Request.Builder()
            .url(uriPath)
            .post(body)
            .build()

        Log.d("responseMsg", "Creating new Eventbridge rule at $uriPath")

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                Log.d("responseMsg", "create rule successful")

                // Sometimes error but returned as success (status code 200)
                val errorMsg = response.body?.string() ?: "none"
                println("error: $errorMsg")
            } else {
                Log.d("responseMsg", "create rule failed")
                Log.d("responsefullMsg", response.code.toString())
                Log.d("responsefullMsg", response.body.toString())
            }
        } catch (e: Exception) {
            Log.d("responseMsg", "code failed")
            e.printStackTrace()
        }

        return
    }

    // Function to get device token and retrieve reminder rules set by this device
    fun getReminderRules(deviceToken: String): Array<ReminderRule>? {

        val json = JSONObject()
        json.put("deviceToken", deviceToken)

        val body = json.toString().toRequestBody(("application/json").toMediaType())

        // Build request path
        val uriPath = "$protocol$getReminderDomain$getReminderPath"
        val request = Request.Builder()
            .url(uriPath)
            .post(body)
            .build()

        Log.d("responseMsg", "Retrieving Eventbridge rules at $uriPath")

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                Log.d("responseMsg", "retrieve rules successful")

                // Convert JSON List to Kotlin
                val jsonResponse = response.body?.string()
                println("Output: $jsonResponse")

                if (!jsonResponse.isNullOrBlank() && jsonResponse != "false") {
                    val jsonList = Json.decodeFromString<Array<ReminderRule>>(jsonResponse)
                    println("Output: $jsonList")

                    return jsonList
                }

            } else {
                Log.d("responseMsg", "retrieve rules failed")
                Log.d("responsefullMsg", response.code.toString())
                Log.d("responsefullMsg", response.body.toString())
            }
        } catch (e: Exception) {
            Log.d("responseMsg", "code failed")
            e.printStackTrace()
        }

        return null
    }

    // Function to get device token
    fun getDeviceToken(): String {

        var token = ""

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("token", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                token = task.result

                Log.d("token on success", token)
            })

        return token
    }
}
package com.nyp.sit.aws.project.onlyplants.Model

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.nyp.sit.aws.project.onlyplants.ReminderFormActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class reminderService {

    // Initialize client and url paths
    private val client = OkHttpClient()
    private val protocol = "https://"
    private val reminderDomain = "d2i8w9vs9g.execute-api.us-east-1.amazonaws.com"
    private val getReminderPath = "/test/createreminderevent"

    // Function to create Eventbridge rule
    fun createReminderRule(cronExp: String) {

        val json = JSONObject()
        json.put("cronExp", cronExp)
        json.put("deviceToken", getDeviceToken())

        val body = json.toString().toRequestBody(("application/json").toMediaType())

        // Build request path
        val uriPath = "$protocol$reminderDomain$getReminderPath"
        val request = Request.Builder()
            .url(uriPath)
            .post(body)
            .build()

        Log.d("responseMsg", "Creating new Eventbridge rule at $uriPath")

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                Log.d("responseMsg", "create rule successful")
            }
            else {
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

                Log.d("token", token)
            })

        return token
    }
}
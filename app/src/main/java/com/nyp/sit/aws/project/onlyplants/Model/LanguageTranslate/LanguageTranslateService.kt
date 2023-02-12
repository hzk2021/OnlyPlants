package com.nyp.sit.aws.project.onlyplants.Model.LanguageTranslate

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request

class LanguageTranslateService {

    private val client = OkHttpClient()
    private val protocol = "https://"
    private val domain = "hk0r447bo2.execute-api.us-east-1.amazonaws.com"
    private val getTranslatedTextPath = "/prod/translate"
    //private val parameters = "?action=query&format=json&list=search&utf8=1&formatversion=2&srsearch="
    //private val postfix = "%20plant"


    suspend fun GetTranslatedText(from_lang: String, to_lang: String, text: String) : String {

        val uriPath = "$protocol$domain$getTranslatedTextPath?from_lang=$from_lang&to_lang=$to_lang&text=$text"
        val request = Request.Builder().url(uriPath).build()

        var translatedText = ""

        Log.d("Getting translated text from", uriPath)

        val response = client.newCall(request).execute()

        if (response.isSuccessful) {
            translatedText = response.body!!.string().replace("\"", "").replace("\\\\u([0-9A-Fa-f]{4})".toRegex()){
                val codePoint = Integer.parseInt(it.groupValues[1],16)
                String(Character.toChars(codePoint))
            }
//            translatedText = String(response.body.string().toString().replace("\"", "").toByteArray(Charsets.ISO_8859_1))
            Log.d("TranslateTextReply",  translatedText)
        } else {
            Log.d("TranslateTextRetrievalError", response.message)
            translatedText = "RetrievalError"
        }

        return translatedText
    }
}
package com.nyp.sit.aws.project.onlyplants.Rekognition

import android.util.Log
import aws.sdk.kotlin.services.rekognition.RekognitionClient
import aws.sdk.kotlin.services.rekognition.model.DetectModerationLabelsRequest
import aws.sdk.kotlin.services.rekognition.model.Image


suspend fun detectModLabels(sourceImage: ByteArray) {

    val myImage = Image {
        this.bytes = sourceImage
    }

    val request = DetectModerationLabelsRequest {
        image = myImage
        minConfidence = 60f
    }

    RekognitionClient { region = "us-east-1" }.use { rekClient ->
        val response = rekClient.detectModerationLabels(request)
        response.moderationLabels?.forEach { label ->
            Log.d(
                "Testing",
                "Label: ${label.name} - Confidence: ${label.confidence} % Parent: ${label.parentName}"
            )
        }
    }
}


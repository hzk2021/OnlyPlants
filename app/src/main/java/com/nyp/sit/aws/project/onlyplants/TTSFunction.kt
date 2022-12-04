package com.nyp.sit.aws.project.onlyplants

import aws.sdk.kotlin.services.polly.PollyClient
import aws.sdk.kotlin.services.polly.model.DescribeVoicesRequest
import aws.sdk.kotlin.services.polly.model.Engine
import aws.sdk.kotlin.services.polly.model.OutputFormat
import aws.sdk.kotlin.services.polly.model.SynthesizeSpeechRequest
import aws.smithy.kotlin.runtime.content.toByteArray
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.regions.Regions
import javazoom.jl.player.FactoryRegistry
import javazoom.jl.player.advanced.AdvancedPlayer
import javazoom.jl.player.advanced.PlaybackEvent
import javazoom.jl.player.advanced.PlaybackListener
import java.io.ByteArrayInputStream


suspend fun TTSFunction() {

    val sample = "Congratulations. You have successfully built this working demo " +
            " of Amazon Polly in Kotlin. Have fun building voice enabled apps with Amazon Polly (that's me!), and always " +
            " look at the AWS website for tips and tricks on using Amazon Polly and other great services from AWS"

    val describeVoiceRequest = DescribeVoicesRequest {
        engine = Engine.Standard
    }

    PollyClient.fromEnvironment() { region = "us-east-1" }.use { polly ->
        val describeVoicesResult = polly.describeVoices(describeVoiceRequest)
        val voice = describeVoicesResult.voices?.get(26)
        polly.synthesizeSpeech(
            SynthesizeSpeechRequest {
                text = sample
                voiceId = voice?.id
                outputFormat = OutputFormat.Mp3
            }
        ) { resp ->

            // inside this block you can access `resp` and play the audio stream.
            val audioData = resp.audioStream?.toByteArray()
            val targetStream = ByteArrayInputStream(audioData)
            val player = AdvancedPlayer(targetStream, FactoryRegistry.systemRegistry().createAudioDevice())
            player.playBackListener = object : PlaybackListener() {
                override fun playbackStarted(evt: PlaybackEvent) {
                    println("Playback started")
                    println(sample)
                }

                override fun playbackFinished(evt: PlaybackEvent) {
                    println("Playback finished")
                }
            }
            // play audio
            player.play()
        }
    }

}
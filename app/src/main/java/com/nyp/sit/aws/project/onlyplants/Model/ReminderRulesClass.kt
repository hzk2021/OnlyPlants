package com.nyp.sit.aws.project.onlyplants.Model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Serializable
data class ReminderRule(
    val Name: String,
    val Arn: String,
    val State: String,
    val ScheduleExpression: String,
    val EventBusName: String,
)

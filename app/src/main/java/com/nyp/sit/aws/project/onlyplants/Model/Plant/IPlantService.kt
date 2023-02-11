package com.nyp.sit.aws.project.onlyplants.Model.Plant

interface IPlantService {
    suspend fun GetPlantInformation(plant_name : String) : String
    suspend fun GetPlantType(base_64_image : String) : String
}
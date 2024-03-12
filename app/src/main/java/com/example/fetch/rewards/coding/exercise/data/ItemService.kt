package com.example.fetch.rewards.coding.exercise.data

import retrofit2.Response
import retrofit2.http.GET

// BaseURL = "https://fetch-hiring.s3.amazonaws.com/"
interface ItemService {
  @GET("hiring.json")
  suspend fun getItems(): Response<List<Item>>
}
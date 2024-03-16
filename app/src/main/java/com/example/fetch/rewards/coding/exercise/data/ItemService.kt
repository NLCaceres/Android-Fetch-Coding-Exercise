package com.example.fetch.rewards.coding.exercise.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

// BaseURL = "https://fetch-hiring.s3.amazonaws.com/"
interface ItemService {
  @Headers("Accept: application/json,*/*;q=0.8")
  @GET("hiring.json")
  suspend fun getItems(): Response<List<Item>>
}
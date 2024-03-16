package com.nlcaceres.fetch.rewards.coding.exercise.utility

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Converter.Factory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun getRetrofitBuilder(): Retrofit = Retrofit.Builder()
  .baseUrl("https://fetch-hiring.s3.amazonaws.com/")
  .addConverterFactory(getGsonConverter())
  .build()

fun getGsonConverter(): Factory = GsonConverterFactory.create(getGsonBuilder())

fun getGsonBuilder(): Gson = GsonBuilder().create()
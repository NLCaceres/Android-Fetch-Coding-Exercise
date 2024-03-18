package com.nlcaceres.fetch.rewards.coding.exercise.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class Item(@PrimaryKey val id: Int, val name: String?, val listId: Int) {
  override fun toString(): String = "Item #$id from List #$listId is " +
      if (name.isNullOrBlank()) "missing a name" else "named '${name.replaceFirstChar(Char::uppercase)}'"
}

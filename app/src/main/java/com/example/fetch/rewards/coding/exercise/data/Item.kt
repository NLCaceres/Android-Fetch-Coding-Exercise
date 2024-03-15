package com.example.fetch.rewards.coding.exercise.data

data class Item(val id: Int, val name: String?, val listId: Int) {
  override fun toString(): String = "Item #$id from List #$listId is " +
      if (name.isNullOrBlank()) "missing a name" else "named '${name.replaceFirstChar(Char::uppercase)}'"
}

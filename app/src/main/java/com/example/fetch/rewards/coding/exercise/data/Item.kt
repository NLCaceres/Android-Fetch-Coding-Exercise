package com.example.fetch.rewards.coding.exercise.data

data class Item(val id: String, val name: String, val listID: String) {
  override fun toString(): String =
    "Item #$id named '${name.replaceFirstChar { it.uppercase() }}' from" + if (listID.isNotBlank()) " List #$listID" else " no list"
}

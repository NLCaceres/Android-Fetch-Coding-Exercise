package com.example.fetch.rewards.coding.exercise.data

import org.junit.Test
import org.junit.Assert.assertEquals

class ItemTests {
  @Test fun `Convert Item to Readable String`() {
    val simpleItem = Item("123", "bar", "12")
    // WHEN the item has a simple ID, name and list ID, THEN it is filled in as follows
    assertEquals(simpleItem.toString(), "Item #123 named 'Bar' from List #12")

    val missingListIdItem = Item("321", "FOO", "")
    // WHEN the name is already all capital letters, THEN it remains the same
    // WHEN the listID is missing, THEN it is noted that it has no List
    assertEquals(missingListIdItem.toString(), "Item #321 named 'FOO' from no list")

    val oddItem = Item("abc", "123", "def")
    // Whether or not the Strings  provided by the API for id, name and listID make sense
    // The Item will use them for its toString() via interpolation anyway
    assertEquals(oddItem.toString(), "Item #abc named '123' from List #def")
  }
}
package com.example.fetch.rewards.coding.exercise.data

import org.junit.Test
import org.junit.Assert.assertEquals

class ItemTests {
  @Test fun `Convert Item to Readable String`() {
    val simpleItem = Item(123, "bar", 12)
    // WHEN the item has a simple ID, name and list ID, THEN it is filled in as follows
    assertEquals(simpleItem.toString(), "Item #123 from List #12 is named 'Bar'")

    val nullNameItem = Item(321, null, 10)
    // WHEN the name is null, THEN the string will mention the name is missing
    assertEquals(nullNameItem.toString(), "Item #321 from List #10 is missing a name")

    val whitespaceNamedItem = Item(135, "    ", 24)
    // WHEN the name is just whitespace (blank or empty), THEN the string will mention the name is missing
    assertEquals(whitespaceNamedItem.toString(), "Item #135 from List #24 is missing a name")
  }
}
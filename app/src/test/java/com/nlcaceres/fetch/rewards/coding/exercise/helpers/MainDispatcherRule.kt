package com.nlcaceres.fetch.rewards.coding.exercise.helpers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

// Reusable JUnit4 TestRule to override the Main Coroutine Dispatcher used during tests
@ExperimentalCoroutinesApi
class MainDispatcherRule(val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()): TestWatcher() {
  override fun starting(description: Description) { Dispatchers.setMain(testDispatcher) }
  override fun finished(description: Description) { Dispatchers.resetMain() }
}
package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("DashReels", appName)
  }

  @Test
  fun `like toggle updates state correctly`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val viewModel = com.example.ui.viewmodel.DashReelsViewModel(context as android.app.Application)

    val dramaId = viewModel.playerState.value.activeDrama.id
    val epNum = viewModel.playerState.value.currentEpisodeIndex + 1
    val key = "${dramaId}_$epNum"

    val initialLiked = viewModel.likedEpisodes.value[key] ?: false
    viewModel.toggleLikeCurrent()
    val afterLiked = viewModel.likedEpisodes.value[key] ?: false

    assertEquals(!initialLiked, afterLiked)
  }
}

package me.fru1t.qbtexporter.settings.impl

import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import me.fru1t.qbtexporter.logger.Logger
import me.fru1t.qbtexporter.settings.Settings
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.io.File

internal class SettingsManagerImplTest {
  private companion object {
    private const val TESTING_DIRECTORY = "test-out/"
    private val SETTINGS_FILE =
      File(TESTING_DIRECTORY + SettingsManagerImpl.DEFAULT_SETTINGS_FILE_LOCATION)
    private val EXAMPLE_SETTINGS_FILE =
      File(TESTING_DIRECTORY + SettingsManagerImpl.EXAMPLE_SETTINGS_FILE_LOCATION)
  }

  private lateinit var gson: Gson
  private lateinit var manager: SettingsManagerImpl
  @Mock private lateinit var mockLogger: Logger

  @BeforeEach
  fun setUp() {
    MockitoAnnotations.initMocks(this)
    gson = Gson()
    manager =
      SettingsManagerImpl(gson = gson, logger = mockLogger, settingsFilePath = TESTING_DIRECTORY)
  }

  @AfterEach
  fun tearDown() {
    SETTINGS_FILE.delete()
    EXAMPLE_SETTINGS_FILE.delete()
    File(TESTING_DIRECTORY).deleteRecursively()
  }

  @Test
  fun get_noExistingSettings_writesDefaultSettings() {
    manager.get()

    assertThat(SETTINGS_FILE.exists()).isTrue()
    assertThat(SETTINGS_FILE.readText()).isNotEmpty()
  }

  @Test
  fun get_invalidFile_writesExampleSettingsFile() {
    SETTINGS_FILE.writeText("garbage { json")

    try {
      manager.get()
      assertWithMessage("Expected a JsonSyntaxException while decoding garbage json")
    } catch (e: JsonSyntaxException) {
      // Expected
    }

    assertThat(EXAMPLE_SETTINGS_FILE.exists()).isTrue()
  }

  @Test
  fun get() {
    SETTINGS_FILE.writeText("{}")

    val settings = manager.get()

    assertThat(settings).isInstanceOf(Settings::class.java)
    assertThat(SETTINGS_FILE.exists()).isTrue()
    assertThat(SETTINGS_FILE.readText()).isEqualTo("{}")
  }

  @Test
  fun save_withoutLoading_doesNothing() {
    manager.save()

    assertThat(SETTINGS_FILE.exists()).isFalse()
  }

  @Test
  fun save() {
    SETTINGS_FILE.writeText("{}")

    manager.get()
    manager.save()

    assertThat(SETTINGS_FILE.exists()).isTrue()
    assertThat(SETTINGS_FILE.readText()).isNotEqualTo("{}")
  }
}

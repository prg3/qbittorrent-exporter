package me.fru1t.qbtexporter.settings.impl

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import me.fru1t.qbtexporter.kotlin.LazyRelay
import me.fru1t.qbtexporter.kotlin.LazyRelayFactory
import me.fru1t.qbtexporter.logger.Logger
import me.fru1t.qbtexporter.settings.Settings
import me.fru1t.qbtexporter.settings.SettingsManager
import java.io.File
import java.io.IOException
import java.lang.RuntimeException
import javax.inject.Inject
import javax.inject.Named

/** Default implementation of [SettingsManager]. */
class SettingsManagerImpl @Inject constructor(
  @Named(NAMED_FILE_PATH) private val settingsFilePath: String,
  private val gson: Gson,
  private val logger: Logger,
  lazyRelayFactory: LazyRelayFactory
) : SettingsManager {
  internal companion object {
    internal const val NAMED_FILE_PATH = "SettingsManagerImpl_filePath"

    internal const val DEFAULT_SETTINGS_FILE_LOCATION: String = "qbt-exporter-settings.json"
    internal const val EXAMPLE_SETTINGS_FILE_LOCATION: String = "example-qbt-exporter-settings.json"
  }

  private val settingsFile: File = File(settingsFilePath + DEFAULT_SETTINGS_FILE_LOCATION)
  private val relay: LazyRelay<Settings> =
    lazyRelayFactory.create({ settingsFile.lastModified() }) {
      try {
        gson.fromJson(settingsFile.reader(), Settings::class.java)
      } catch (e: JsonSyntaxException) {
        logger.e("Failed to read the settings file at $DEFAULT_SETTINGS_FILE_LOCATION.")
        writeExampleSettings()
        logger.i("But I'm still gonna crash so you can figure out the issue.")
        throw e
      }
    }

  init {
    val settingsDir = File(settingsFilePath)
    if (!settingsDir.exists()) {
      settingsDir.mkdirs()
    }

    // Write settings file if none exists
    if (!settingsFile.exists()) {
      logger.i("No settings file found, creating one at ${settingsFile.absoluteFile}.")
      settingsFile.writeText(gson.toJson(Settings()))
    } else {
      logger.i("Settings found, will load from $DEFAULT_SETTINGS_FILE_LOCATION")
    }

    if (!settingsFile.canRead() && !settingsFile.canWrite()) {
      throw RuntimeException(
        "I need read and write access to $DEFAULT_SETTINGS_FILE_LOCATION to work."
      )
    }
  }

  override fun getSettingsRelay(): LazyRelay<Settings> = relay

  /** Writes the default settings to an example file. */
  private fun writeExampleSettings() {
    val exampleSettingsFile = File(settingsFilePath + EXAMPLE_SETTINGS_FILE_LOCATION)
    if (!exampleSettingsFile.canWrite()) {
      logger.i(
        "I can't even write an example settings file for you as I don't have write permissions " +
            "to $settingsFilePath$EXAMPLE_SETTINGS_FILE_LOCATION."
      )
    }

    logger.i(
      "Trying to write an example settings file at " + exampleSettingsFile.absoluteFile)
    try {
      exampleSettingsFile.writeText(gson.toJson(Settings()))
      logger.i("Successfully wrote example settings!")
    } catch (e: IOException) {
      logger.e("Nevermind. Something bad happened while writing the example settings:")
      throw e
    }
  }
}

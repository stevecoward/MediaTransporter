package io.sugarstack.mediatransporter

import com.natpryce.konfig.*
import java.io.File
import kotlin.system.exitProcess

private val logger = TransportLogger()

interface ConfigInterface {
    var propertiesData: ConfigurationProperties
    var propertiesMapList: MutableList<MutableMap<String, Any>>
    var properties: MutableMap<String, Any>
}

/**
 * An object that holds user configuration properties.
 */
object Config : ConfigInterface {
    override lateinit var propertiesData: ConfigurationProperties
    override var propertiesMapList: MutableList<MutableMap<String, Any>> = mutableListOf()
    override var properties: MutableMap<String, Any> = mutableMapOf()

    /**
     * Reads a properties file [propertiesFile] and adds the values to a ConfigurationProperties object.
     */
    operator fun invoke(propertiesFile: File): Config {
        try {
            propertiesData = ConfigurationProperties.fromFile(propertiesFile)
        } catch (e: Exception) {
            logger.error("Properties file $propertiesFile was not found. Copy the sample, modify and rerun")
            exitProcess(-1)
        }

        propertiesMapList.add(mutableMapOf("key" to "percentageSafeCapacity", "type" to intType))
        propertiesMapList.add(mutableMapOf("key" to "mediaShareMount", "type" to stringType))
        propertiesMapList.add(mutableMapOf("key" to "completedDownloadPath", "type" to stringType))
        propertiesMapList.add(mutableMapOf("key" to "tvSeasonPath", "type" to stringType))
        propertiesMapList.add(mutableMapOf("key" to "movieRootPath", "type" to stringType))
        propertiesMapList.add(mutableMapOf("key" to "regexMoviePattern", "type" to stringType))
        propertiesMapList.add(mutableMapOf("key" to "regexTvPattern", "type" to stringType))

        for (propertyMap in propertiesMapList) {
            val propertyValue = propertiesData[Key(
                propertyMap["key"] as String,
                propertyMap["type"] as (PropertyLocation, String) -> Any
            )]
            properties[propertyMap["key"] as String] = propertyValue
        }

        return this
    }
}


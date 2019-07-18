package io.sugarstack.mediatransporter.filesystem

import io.sugarstack.mediatransporter.Config
import io.sugarstack.mediatransporter.TransportLogger
import java.io.File
import java.math.RoundingMode
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.system.exitProcess

private val logger = TransportLogger()

/**
 * Holds functionality that performs various filesystem functions.
 */
class Storage {

    private lateinit var mediaShare: Path
    lateinit var completedDownloadsPath: Path

    /**
     * Enumerates available mounts and validate that they exist.
     */
    init {
        val mediaShares = Config.properties["mediaShareMount"] as String
        val mediaSharesList: List<String> = mediaShares.split(",")
        for(share in mediaSharesList) {
            val sharePath = Paths.get(share)
            if(Files.exists(sharePath)) {
                mediaShare = sharePath
                break
            }
        }

        val downloadsPath = Paths.get(Config.properties["completedDownloadPath"] as String)
        if(Files.exists(downloadsPath)) {
            completedDownloadsPath = downloadsPath
        }

    }

    /**
     * Gathers disk capacity of 'mediaShare' and returns true or false if the disk capacity
     * exceeds user-set property Config.properties["percentageSafeCapacity"].
     */
    private fun isCapacityReached(): Boolean {
        val volume = File(mediaShare.toString())
        val totalBytes = volume.totalSpace
        val totalBytesFree = volume.freeSpace
        val capacity: Int = (((totalBytes - totalBytesFree).toDouble() / totalBytes) * 100)
            .toBigDecimal()
            .setScale(1, RoundingMode.UP)
            .toInt()

        if (capacity >= Config.properties["percentageSafeCapacity"] as Int) return true
        return false
    }

    /**
     * Handles informing the user whether the mount disk capacity is good or not.
     */
    fun determineCapacity() {
        try {
            if (isCapacityReached()) {
                logger.warn("Capacity for storage volume: $mediaShare reached. Exiting.")
                exitProcess(-1)
            } else {
                logger.info("Capacity looks good, continuing")
            }
        } catch (e: Exception) {
            logger.error("Could not locate the share defined in config!")
            exitProcess(-1)
        }
    }
}
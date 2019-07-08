package io.sugarstack.mediatransporter

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class Storage {

    lateinit var mediaShare: Path
    lateinit var completedDownloadsPath: Path

    init {
        val mediaShares = Config.mediaShareMount
        val mediaSharesList: List<String> = mediaShares.split(",")
        for(share in mediaSharesList) {
            val sharePath = Paths.get(share)
            if(Files.exists(sharePath)) {
                mediaShare = sharePath
                break
            }
        }

        val downloadsPath = Paths.get(Config.completedDownloadPath)
        if(Files.exists(downloadsPath)) {
            completedDownloadsPath = downloadsPath
        }

    }

    private fun getVolumeCapacity(): List<String> {
        val capacityCommandParameters = "df -H $mediaShare | tail -n1 | awk '{ print \$5 \" \" \$4 }'"
        val capacityCommandOutput = Utils.execLocal(capacityCommandParameters)

        return capacityCommandOutput.split(" ")
    }

    fun isCapacityReached(): Boolean {
        val capacityDetails = getVolumeCapacity()
        val capacityPercentage: Int = capacityDetails[0].replace("%", "").toInt()

        if(capacityPercentage >= Config.percentageSafeCapacity) return true
        return false
    }
}
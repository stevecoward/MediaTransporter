package io.sugarstack.mediatransporter

import java.lang.Exception

class MediaTransporter {

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            val storage = Storage()
            try {
                if(storage.isCapacityReached()) {
                    println("Capacity for storage volume: ${storage.mediaShare} reached. Exiting.")
                    return
                } else {
                    println("Capacity is good, continuing")
                }
            } catch(e: Exception) {
                println("Could not locate the share defined in config!")
                return
            }

            Utils.findMediaFiles(storage.completedDownloadsPath)

//            val title = args[0]
//            val config = Config
//            if(config.regexMoviePattern.matches(title)) {
//                println("Found a valid movie!")
//            } else {
//                println("Not a valid movie!")
//            }
        }
    }
}
package io.sugarstack.mediatransporter

import io.sugarstack.mediatransporter.filesystem.ListFiles
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import kotlin.text.Charsets.UTF_8

/**
 * Contains a collection of methods used by the program to perform various functions.
 */
object Utils {

    /**
     * Locates files from [mediaFilesPath] optionally including rar archives in the search with [includeArchiveExtension]
     * matching [regexPartial] passed from MediaTransporter's entry point. Will return a MutableList<Paths> object of
     * matches.
     */
    fun findMediaFiles(
        mediaFilesPath: Path,
        includeArchiveExtension: Boolean = false,
        regexPartial: String?
    ): MutableList<Path> {
        val regexNamePartial = when (regexPartial.isNullOrBlank()) {
            true -> ".+"
            false -> regexPartial
        }

        val globFilesPattern = when (includeArchiveExtension) {
            true -> "$regexNamePartial\\.(?:(?:mkv)|(?:avi)|(?:mp4)|(?:mov)|(?:rar))\$"
            false -> "^$regexNamePartial\\.(?:(?:mkv)|(?:avi)|(?:mp4)|(?:mov))\$"
        }

        val matcher = FileSystems
            .getDefault()
            .getPathMatcher("regex:$globFilesPattern")

        val listFiles = ListFiles(matcher)
        Files.walkFileTree(mediaFilesPath, listFiles)

        return listFiles.getFoundFiles()
    }

    /**
     * Converts a release [title] into a better formatted string and returns the formatted String.
     */
    fun toTitleCase(title: String): String {
        val finalTitleWords: MutableList<String> = ArrayList()
        val words = title.split(" ")
        val shortWords = arrayListOf(
            "a", "an", "the", "and", "but", "or", "for", "nor", "on", "at", "to", "from", "by", "of"
        )

        for (word in words) {
            if (word !in shortWords) {
                finalTitleWords.add(StringUtils.capitalize(word))
            } else {
                when {
                    words.indexOf(word) == 0 -> finalTitleWords.add(StringUtils.capitalize(word))
                    else -> finalTitleWords.add(word)
                }
            }
        }

        return finalTitleWords.joinToString(" ")
    }
}
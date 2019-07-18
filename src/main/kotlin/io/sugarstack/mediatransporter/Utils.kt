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

private val logger = TransportLogger()

/**
 * Contains a collection of methods used by the program to perform various functions.
 */
object Utils {

    /**
     * Holds data from [inputStream] and returns a String of the output.
     */
    private fun collectOutput(inputStream: InputStream): String {
        val out = StringBuilder()
        val buf: BufferedReader = inputStream.bufferedReader(UTF_8)
        var line = buf.readLine()
        do {
            if (line != null) {
                out.append(line).append("\n")
            }
            line = buf.readLine()
        } while (line != null)
        return out.toString()
    }

    /**
     * Passes [command] to execLocal() with default values and returns the results of a command.
     */
    fun execLocal(command: String): String {
        return execLocal(command, hashMapOf())
    }

    /**
     * Performs a system [command] using 'sh'. Generally unsafe and not in use, so will be removed.
     */
    private fun execLocal(command: String, env: HashMap<String, String>): String {
        try {
            val commands = ArrayList<String>()
            commands.add("sh")
            commands.add("-c")
            commands.add(command)

            val pb = ProcessBuilder(commands)
            pb.environment().putAll(env)

            val process = pb.start()
            val result = collectOutput(process.inputStream)
            val errors = IOUtils.toString(process.errorStream, Charset.forName("utf-8"))

            if (process.waitFor() != 0) {
                logger.error("Failed to execute command ${pb.command()}.\nstderr: $errors\nstdout: $result")
                throw RuntimeException(errors)
            }
            return result
        } catch (e: IOException) {
            throw RuntimeException(e)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
    }

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
            "a", "an", "the", "and", "but", "or", "for", "nor", "on", "at", "to", "from", "by"
        )

        for (word in words) {
            if (word !in shortWords) {
                finalTitleWords.add(StringUtils.capitalize(word))
            } else {
                finalTitleWords.add(word)
            }
        }

        return finalTitleWords.joinToString(" ")
    }
}
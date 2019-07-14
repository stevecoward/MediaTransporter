package io.sugarstack.mediatransporter

import io.sugarstack.mediatransporter.filesystem.ListFiles
import mu.KotlinLogging
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

private val logger = KotlinLogging.logger {}
object Utils {

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

    fun execLocal(command: String): String {
        return execLocal(command, hashMapOf())
    }

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
                logger.error { "Failed to execute command ${pb.command()}.\nstderr: $errors\nstdout: $result" }
                throw RuntimeException(errors)
            }
            return result
        } catch (e: IOException) {
            throw RuntimeException(e)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
    }

    fun findMediaFiles(
        mediaFilesPath: Path,
        includeArchiveExtension: Boolean = false,
        regexFileNamePartial: String = ".+"
    ): List<Path> {
        var globFilesPattern = "^$regexFileNamePartial\\.(?:(?:mkv)|(?:avi)|(?:mp4)|(?:mov))\$"
        if (includeArchiveExtension) globFilesPattern =
            "$regexFileNamePartial\\.(?:(?:mkv)|(?:avi)|(?:mp4)|(?:mov)|(?:rar))\$"
        val matcher = FileSystems
            .getDefault()
            .getPathMatcher("regex:$globFilesPattern")

        val listFiles = ListFiles(matcher)
        Files.walkFileTree(mediaFilesPath, listFiles)

        return listFiles.getFoundFiles()
    }

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
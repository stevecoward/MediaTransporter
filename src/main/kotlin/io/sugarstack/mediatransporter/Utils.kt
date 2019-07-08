package io.sugarstack.mediatransporter

import org.apache.commons.io.IOUtils
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import java.nio.file.*
import kotlin.text.Charsets.UTF_8


object Utils {
    private fun collectOutput(inputStream: InputStream): String {
        val out = StringBuilder()
        val buf: BufferedReader = inputStream.bufferedReader(UTF_8)
        var line: String? = buf.readLine()
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

    fun execLocal(command: String, env: HashMap<String, String>): String {
        try {
            val commands = ArrayList<String>()
            commands.add("sh")
            commands.add("-c")
            commands.add(command)

            val pb = ProcessBuilder(commands)
            pb.environment().putAll(env)
//            println("Running: ${pb.command()} \n with env $env")

            val process = pb.start()
            val result = collectOutput(process.inputStream)
            val errors = IOUtils.toString(process.errorStream, Charset.forName("utf-8"))

            if (process.waitFor() != 0) {
                println("Failed to execute command ${pb.command()}.\nstderr: $errors\nstdout: $result")
                throw RuntimeException(errors)
            } else {
//                println("stdout: $result")
            }
            return result
        } catch (e: IOException) {
            throw RuntimeException(e)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
    }

    fun findMediaFiles(completedDownloadsPath: Path): Boolean {
        val globFilesPattern = "*.{mkv,avi,mp4,mov}"
        val matcher = FileSystems
            .getDefault()
            .getPathMatcher("glob:$globFilesPattern")

        val listFiles = ListFiles(matcher)
        Files.walkFileTree(completedDownloadsPath, listFiles)

        val foundFiles = listFiles.getFoundFiles()

        return true
    }
}
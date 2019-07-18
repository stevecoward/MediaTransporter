package io.sugarstack.mediatransporter.filesystem

import io.sugarstack.mediatransporter.Config
import java.nio.file.FileVisitResult
import java.nio.file.Path
import java.nio.file.PathMatcher
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

/**
 * Utility object to handle finding files in a path.
 *
 * @property pathMatcher regular expression matcher object matching media file extensions.
 */
class ListFiles(private val pathMatcher: PathMatcher) : SimpleFileVisitor<Path>() {
    private var foundFiles: MutableList<Path> = ArrayList()

    /**
     * Overrides visitFile() method that attempts to find [file] optionally containing [attrs].
     */
    override fun visitFile(file: Path, attrs: BasicFileAttributes?): FileVisitResult {
        findFile(file, pathMatcher)
        return FileVisitResult.CONTINUE
    }

    /**
     * Overrides the SimpleFileVisitor<Path> class method that is used recursively to
     * find media files in a [dir] path. BasicFileAttributes object [attrs] is not used
     * in this implementation.
     */
    override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes?): FileVisitResult {
        val directory = dir.toString()
        if ((directory != Config.properties["completedDownloadPath"] as String)) {
            findFile(dir, pathMatcher)
        }

        return FileVisitResult.CONTINUE
    }

    /**
     * Returns a MutableList<Path> object of [foundFiles] gathered with findFile().
     */
    fun getFoundFiles(): MutableList<Path> {
        return foundFiles
    }

    /**
     * Adds files to MutableList<Path> 'foundFiles' if the [filePath] doesn't contain 'sample'.
     * We do not need to process sample files during processing. A [matcher] matching files with
     * media extensions is then used to populate the 'foundFiles' List.
     */
    private fun findFile(filePath: Path, matcher: PathMatcher) {
        val fileName = filePath.fileName

        if (!fileName.toString().startsWith("sample", true)) {
            if (matcher.matches(fileName.fileName)) {
                foundFiles.add(filePath.toAbsolutePath())
            }
        }
    }
}
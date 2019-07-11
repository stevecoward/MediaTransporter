package io.sugarstack.mediatransporter

import java.nio.file.FileVisitResult
import java.nio.file.Path
import java.nio.file.PathMatcher
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

class ListFiles(private val pathMatcher: PathMatcher) : SimpleFileVisitor<Path>() {
    private var foundFiles: MutableList<Path> = ArrayList()

    override fun visitFile(file: Path, attrs: BasicFileAttributes?): FileVisitResult {
        findFile(file, pathMatcher)
        return FileVisitResult.CONTINUE
    }

    override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes?): FileVisitResult {
        val directory = dir.toString()
        if ((directory != Config.completedDownloadPath)) {
            findFile(dir, pathMatcher)
        }

        return FileVisitResult.CONTINUE
    }

    fun getFoundFiles(): MutableList<Path> {
        return foundFiles
    }

    private fun findFile(filePath: Path, matcher: PathMatcher) {
        val fileName = filePath.fileName

        if (!fileName.toString().startsWith("sample", true)) {
            if (matcher.matches(fileName.fileName)) {
                foundFiles.add(filePath.toAbsolutePath())
            }
        }
    }
}
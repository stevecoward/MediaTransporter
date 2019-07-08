package io.sugarstack.mediatransporter

import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.Path
import java.nio.file.PathMatcher
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

class ListFiles(val pathMatcher: PathMatcher): SimpleFileVisitor<Path>() {
    private var foundFiles: MutableList<Path> = ArrayList()

    override fun visitFile(file: Path?, attrs: BasicFileAttributes?): FileVisitResult {
        findFile(file, pathMatcher)
        return FileVisitResult.CONTINUE
    }

    override fun preVisitDirectory(dir: Path?, attrs: BasicFileAttributes?): FileVisitResult {
        findFile(dir, pathMatcher)
        return FileVisitResult.CONTINUE
    }

    fun getFoundFiles(): MutableList<Path> {
        return foundFiles
    }

    private fun findFile(filePath: Path?, matcher: PathMatcher) {
        val fileName = filePath?.fileName

        if(matcher.matches(fileName?.fileName)) {
            println("Found matching file: $fileName")
            foundFiles.add(filePath!!.toAbsolutePath())
        }
    }
}
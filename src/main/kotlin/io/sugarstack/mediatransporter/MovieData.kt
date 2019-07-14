package io.sugarstack.mediatransporter

import java.nio.file.Path

data class MovieData(var title: String, val year: String, val resolution: String, val path: Path) {
    lateinit var sharePath: Path

    init {
        title = Utils.toTitleCase(title.replace(".", " "))
    }

    override fun toString(): String {
        return "MovieData(title='$title', year='$year', resolution='$resolution', path=${path.fileName})"
    }
}
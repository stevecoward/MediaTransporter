package io.sugarstack.mediatransporter

import java.nio.file.Path

data class MovieData(val title: String, val year: String, val resolution: String, val path: Path) {
    override fun toString(): String {
        return "MovieData(title='$title', year='$year', resolution='$resolution', path=${path.fileName})"
    }
}
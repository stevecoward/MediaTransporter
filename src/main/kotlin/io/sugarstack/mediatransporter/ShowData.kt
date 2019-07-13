package io.sugarstack.mediatransporter

import java.nio.file.Path

data class ShowData(var title: String, val season: String, val episode: String, val path: Path) {
    lateinit var sharePath: Path

    init {
        title = Utils.toTitleCase(title.replace(".", " "))
    }

    override fun toString(): String {
        return "ShowData(title='$title', season='$season', episode='$episode', path=${path.fileName})"
    }
}
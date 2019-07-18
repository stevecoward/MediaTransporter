package io.sugarstack.mediatransporter.media.data

import io.sugarstack.mediatransporter.Utils
import java.nio.file.Path

/**
 * Holds data pertaining to shows that were captured by regular expression: Config.properties["regexTvPattern"].
 *
 * @property title name of the release.
 * @property season value captured matching '\[sS\]\d\d'.
 * @property episode value captured matching '\[eE\]\d\d'.
 * @property path absolute path to the release media file.
 */
data class ShowData(var title: String, val season: String, val episode: String, val path: Path) {
    lateinit var sharePath: Path

    /**
     * Changes the release title's periods with spaces.
     */
    init {
        title = Utils.toTitleCase(title.replace(".", " "))
    }

    /**
     * Override method to return a human-readable String of a ShowData object.
     */
    override fun toString(): String {
        return "ShowData(title='$title', season='$season', episode='$episode', path=${path.fileName})"
    }
}
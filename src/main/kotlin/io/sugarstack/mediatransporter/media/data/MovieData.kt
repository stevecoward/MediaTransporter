package io.sugarstack.mediatransporter.media.data

import io.sugarstack.mediatransporter.Utils
import java.nio.file.Path

/**
 * Holds data pertaining to movies that were captured by regular expression: Config.properties["regexMoviePattern"]
 *
 * @property title name of the release
 * @property year is the release year
 * @property resolution release resolution
 * @property path absolute path to the release media file
 */
data class MovieData(var title: String, val year: String, val resolution: String, val path: Path) {
    lateinit var sharePath: Path

    /**
     * Changes the release title's periods with spaces.
     */
    init {
        title = Utils.toTitleCase(title.replace(".", " "))
    }

    /**
     * Override method to return a human-readable String of a MovieData object.
     */
    override fun toString(): String {
        return "MovieData(title='$title', year='$year', resolution='$resolution', path=${path.fileName})"
    }
}
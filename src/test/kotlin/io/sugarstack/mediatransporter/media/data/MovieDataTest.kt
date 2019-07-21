package io.sugarstack.mediatransporter.media.data

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.Test
import java.nio.file.Path

class MovieDataTest {
    private val path = mock<Path>()
    private val movie = MovieData("gullivers.travels", "1939", "720p", path)

    @Test
    fun movieDataReleaseFilenameTitleIsConvertedToTitleCase() {
        assertThat("Gullivers Travels", equalTo(movie.title))
    }

    @Test
    fun showDataToStringMatch() {
        assertThat("MovieData(title='Gullivers Travels', year='1939', resolution='720p', path=null)", equalTo(movie.toString()))
    }
}
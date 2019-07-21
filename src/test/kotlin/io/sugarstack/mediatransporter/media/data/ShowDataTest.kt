package io.sugarstack.mediatransporter.media.data

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.Test
import java.nio.file.Path

class ShowDataTest {
    private val path = mock<Path>()
    private var show = ShowData("hogans.heros", "s01", "e01", path)

    @Test
    fun showDataReleaseFilenameTitleIsConvertedToTitleCase() {
        assertThat("Hogans Heros", equalTo(show.title))
    }

    @Test
    fun showDataToStringMatch() {
        assertThat("ShowData(title='Hogans Heros', season='s01', episode='e01', path=null)", equalTo(show.toString()))
    }
}
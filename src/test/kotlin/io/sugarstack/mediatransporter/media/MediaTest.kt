package io.sugarstack.mediatransporter

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.isA
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.whenever
import io.sugarstack.mediatransporter.Config.invoke
import io.sugarstack.mediatransporter.media.Media
import io.sugarstack.mediatransporter.media.data.MovieData
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MediaTest {
    private val path = mock<Path>()
    private val testPropertiesPath = Paths.get(System.getProperty("user.dir")).resolve("src/test/resources/mediatransporter.properties").toFile()
    lateinit var sharePath: Path
    private val movieData = MovieData("Lawful Larceny", "1930", "2160p", path)

    @BeforeAll
    fun setupConfigProperties() {
        mock<Config> { invoke(testPropertiesPath) }
        val foo = mock<File>()

        whenever(mock<File>().mkdirs()).thenReturn(true)

        val mediaShareMount = Paths.get(System.getProperty("user.dir"))
            .resolve("src/test/resources/media-share")
            .toAbsolutePath()
            .toString()

        Config.properties["mediaShareMount"] = mediaShareMount

//        sharePath = Paths.get(
//            String.format(
//                Config.properties["movieRootPath"] as String,
//                Config.properties["mediaShareMount"] as String,
//                movieData.title
//            )
//        )
    }

    @Test
    fun checkFilesExist() {
        val media = Media(movieData)
        val x = ""

    }
}
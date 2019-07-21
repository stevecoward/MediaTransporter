package io.sugarstack.mediatransporter

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.isA
import com.nhaarman.mockitokotlin2.mock
import io.sugarstack.mediatransporter.Config.invoke
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UtilTest {
    private val utils = Utils
    private val testPropertiesPath = Paths.get(System.getProperty("user.dir")).resolve("src/test/resources/mediatransporter.properties").toFile()
    private val downloadsDirectory = Paths.get(System.getProperty("user.dir"))
        .resolve("src/test/resources/media-downloads")
        .toAbsolutePath()
        .toString()

    @BeforeAll
    fun setupConfigProperties() {
        if(!Files.isDirectory(Paths.get(downloadsDirectory))) throw NoSuchFileException(File(downloadsDirectory))
        mock<Config> { invoke(testPropertiesPath) }

        Config.properties["completedDownloadPath"] = downloadsDirectory
    }

    @Test
    fun findMediaFilesWithArchiveExtensionWithoutRegex() {
        val foundList = utils.findMediaFiles(
            Paths.get(Config.properties["completedDownloadPath"] as String), true, ""
        )

        assertThat(8, equalTo(foundList.size))
    }

    @Test
    fun findMediaFilesWithoutArchiveExtensionWithoutRegex() {
        val foundList = utils.findMediaFiles(
            Paths.get(Config.properties["completedDownloadPath"] as String), false, ""
        )

        assertThat(3, equalTo(foundList.size))
    }

    @Test
    fun findMediaFilesWithoutArchiveExtensionWithNullRegex() {
        val foundList = utils.findMediaFiles(
            Paths.get(Config.properties["completedDownloadPath"] as String), false, null
        )

        assertThat(3, equalTo(foundList.size))
    }

    @Test
    fun findMediaFilesWithoutArchiveExtensionWithShowRegex() {
        val showRegex = ".+[sS]02[eE]01.+"
        val foundList = utils.findMediaFiles(
            Paths.get(Config.properties["completedDownloadPath"] as String), false, showRegex
        )

        assertThat(1, equalTo(foundList.size))
    }

    @Test
    fun findMediaFilesReturnsArrayList() {
        val foundList = utils.findMediaFiles(
            Paths.get(Config.properties["completedDownloadPath"] as String), false, null
        )

        assertThat(foundList::class.java, isA(equalTo(ArrayList<Path>()::class.java)))
    }

    @Test
    fun convertToTitleCase() {
        val utils = Utils
        val provided = "the title of a show"
        val expected = "The Title of a Show"

        assertThat(expected, equalTo(utils.toTitleCase(provided)))
    }
}
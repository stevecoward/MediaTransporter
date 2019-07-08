package io.sugarstack.mediatransporter

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class Media(var title: String, var fileName: String, var downloadPath: String) {
    init {
        val fullFilePath: Path = Paths.get("$downloadPath/$fileName")
        if(Files.exists(fullFilePath)) {

        }
    }
}
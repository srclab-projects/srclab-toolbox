package xyz.srclab.untitled.source

import java.io.File
import java.nio.file.Files

/**
 * @author sunqian
 */
interface SourceConverter {

    fun convert(
        sourcePath: String,
        destPath: String,
        replaceDir: Map<String, String>,
        replaceContent: Map<String, String>,
        filter: (path: String) -> Boolean
    )

    companion object {

        @JvmField
        val DEFAULT: SourceConverter = SourceConverterImpl()
    }
}

private class SourceConverterImpl : SourceConverter {
    override fun convert(
        sourcePath: String,
        destPath: String,
        replaceDir: Map<String, String>,
        replaceContent: Map<String, String>,
        filter: (path: String) -> Boolean
    ) {
        fun walkPath(relativePath: String) {
            val srPathString = sourcePath + relativePath
            val drPathString = destPath + relativePath
            val file = File(srPathString)
            val sublist = file.list()
            if (sublist === null && !filter(srPathString)) {
                return
            }
            if (sublist === null) {
                var actualDrPathString = drPathString
                for (entry in replaceDir) {
                    if (actualDrPathString.contains(entry.key)) {
                        actualDrPathString = actualDrPathString.replace(entry.key, entry.value)
                    }
                }
                var content = Files.readString(file.toPath())
                for (entry in replaceContent) {
                    content = content.replace(entry.key, entry.value)
                }
                val drFile = File(actualDrPathString)
                if (!drFile.exists()) {
                    drFile.parentFile.mkdirs()
                }
                println(">>$drFile")
                Files.write(drFile.toPath(), content.toByteArray())
            }
            if (sublist !== null) {
                for (sub in sublist) {
                    walkPath("$relativePath/$sub")
                }
            }
        }

        walkPath("")
    }
}
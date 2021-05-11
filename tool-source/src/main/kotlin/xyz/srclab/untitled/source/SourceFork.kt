package xyz.srclab.untitled.source

import xyz.srclab.common.test.TestLogger
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createFile

/**
 * @author sunqian
 */
interface SourceFork {

    fun fork(from: Path, to: Path, handler: (from: ForkFile, to: Path) -> ForkFile?)

    interface ForkFile {
        val path: Path
        val content: String
    }

    companion object {

        private val logger: TestLogger = TestLogger.DEFAULT

        val DEFAULT: SourceFork = SourceForkImpl()

        private class SourceForkImpl : SourceFork {

            override fun fork(from: Path, to: Path, handler: (from: ForkFile, to: Path) -> ForkFile?) {
                val fromFile = from.toFile()
                if (fromFile.isFile) {
                    val toForkFile = handler(fromFile.toForkFile(from), to)
                    if (toForkFile === null) {
                        return
                    }
                    logger.log("fork $from to $to")
                    toForkFile.path.parent.toFile().mkdirs()
                    Files.writeString(toForkFile.path, toForkFile.content)
                    return
                }
                if (fromFile.isDirectory) {
                    val subFromFiles = fromFile.list()
                    if (subFromFiles === null) {
                        return
                    }
                    for (listFile in subFromFiles) {
                        fork(from.resolve(listFile), to.resolve(listFile), handler)
                    }
                }
            }

            private fun File.toForkFile(path: Path = this.toPath()): ForkFile {
                return object : ForkFile {
                    override val path: Path = path
                    override val content: String by lazy {
                        Files.readString(path)
                    }
                }
            }
        }
    }
}
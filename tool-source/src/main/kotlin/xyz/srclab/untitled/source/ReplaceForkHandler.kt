package xyz.srclab.untitled.source

import org.springframework.util.AntPathMatcher
import java.nio.file.Path

fun replaceFork(
    replaceDirectories: Map<String, String>,
    replaceContents: Map<String, String>,
    filter: Iterable<String>,
    filterNot: Iterable<String>
): (from: SourceFork.ForkFile, to: Path) -> SourceFork.ForkFile? {
    return ReplaceForkHandler(replaceDirectories, replaceContents, filter, filterNot).handler
}

class ReplaceForkHandler(
    replaceDirectories: Map<String, String>,
    replaceContents: Map<String, String>,
    filter: Iterable<String>,
    filterNot: Iterable<String>
) {

    private val antPathMatcher: AntPathMatcher = AntPathMatcher()

    val handler: (from: SourceFork.ForkFile, to: Path) -> SourceFork.ForkFile? = label@{ from, toPath ->
        val fromPathString = from.path.toString()
        var passFilter = false
        for (s in filter) {
            if (antPathMatcher.match(s, fromPathString)) {
                passFilter = true
                break
            }
        }
        if (!passFilter) {
            return@label null
        }
        for (s in filterNot) {
            if (antPathMatcher.match(s, fromPathString)) {
                return@label null
            }
        }
        var toPathString = toPath.toString()
        for (replaceDirectory in replaceDirectories) {
            toPathString = toPathString.replace(replaceDirectory.key, replaceDirectory.value)
        }
        var toContent = from.content
        for (replaceContent in replaceContents) {
            toContent = toContent.replace(replaceContent.key, replaceContent.value)
        }
        object : SourceFork.ForkFile {
            override val path: Path = Path.of(toPathString)
            override val content: String = toContent
        }
    }
}
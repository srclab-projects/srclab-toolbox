package test.xyz.srclab.untitled.source

import org.testng.annotations.Test
import org.yaml.snakeyaml.Yaml
import xyz.srclab.common.base.asAny
import xyz.srclab.common.base.loadStringResource
import xyz.srclab.untitled.source.SourceFork
import xyz.srclab.untitled.source.replaceFork
import java.nio.file.Path

/**
 * @author sunqian
 */
class SourceForkTest {

    private val converter = SourceFork.DEFAULT

    @Test
    fun convertXteamBom() {
        convert("xhome-dependencies")
    }

    @Test
    fun convertXtea() {
        convert("xtea")
    }

    @Test
    fun convertXteaSpringBoot() {
        convert("xtea-spring-boot")
    }

    @Test
    fun convertGrpcSpringBoot() {
        convert("grpc-spring-boot")
    }

    private fun convert(name: String) {

        fun Any?.asStringList(): List<String> {
            return this?.asAny() ?: emptyList()
        }

        fun Any?.asStringMap(): Map<String, String> {
            return this?.asAny() ?: emptyMap()
        }

        val yamlStr = "source.yml".loadStringResource()
        val yaml = Yaml()
        val all: Map<String, Map<String, Any>> = yaml.load(yamlStr)
        val target = all[name]!!
        val replaceDirectories: Map<String, String> = target["replaceDirectories"].asStringMap()
        val replaceContents: Map<String, String> = target["replaceContents"].asStringMap()
        val filter = target["filter"].asStringList()
        val filterNot = target["filterNot"].asStringList()
        converter.fork(
            Path.of(target["from"] as String),
            Path.of(target["to"] as String),
            replaceFork(replaceDirectories, replaceContents, filter, filterNot)
        )
    }
}
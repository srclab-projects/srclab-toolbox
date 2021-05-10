package test.xyz.srclab.untitled.source

import org.springframework.util.AntPathMatcher
import org.testng.annotations.Test
import org.yaml.snakeyaml.Yaml
import xyz.srclab.common.base.asAny
import xyz.srclab.common.base.loadStringResource
import xyz.srclab.untitled.source.SourceConverter

/**
 * @author sunqian
 */
class SourceConverterTest {

    private val converter = SourceConverter.DEFAULT

    private val antPathMatcher = AntPathMatcher()

    @Test
    fun convertXteamBom() {
        convert("xteam-dependencies")
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
        val filter = target["filter"].asStringList()
        val filterNot = target["filterNot"].asStringList()
        converter.convert(
            target["sourcePath"] as String,
            target["destPath"] as String,
            target["replaceDir"].asStringMap(),
            target["replaceContent"].asStringMap(),
        ) {
            for (s in filterNot) {
                if (antPathMatcher.match(s, it)) {
                    return@convert false
                }
            }
            for (s in filter) {
                if (antPathMatcher.match(s, it)) {
                    return@convert true
                }
            }
            false
        }
    }
}
package test.xyz.srclab.untitled.source

import org.springframework.util.AntPathMatcher
import org.testng.annotations.Test
import org.yaml.snakeyaml.Yaml
import xyz.srclab.common.base.loadStringResource
import xyz.srclab.untitled.source.SourceConverter

/**
 * @author sunqian
 */
class SourceConverterTest {

    private val converter = SourceConverter.DEFAULT

    private val antPathMatcher = AntPathMatcher()

    @Test
    fun convertXtea() {
        convert("xt")
    }

    @Test
    fun convertXteaSpringBoot() {
        convert("xt-spring-boot")
    }

    private fun convert(name: String) {
        val yamlStr = "source.yml".loadStringResource()
        val yaml = Yaml()
        val all: Map<String, Map<String, Any>> = yaml.load(yamlStr)
        val target = all[name]!!
        val filter = target["filter"] as List<String>
        val filterNot = target["filterNot"] as List<String>
        converter.convert(
            target["sourcePath"] as String,
            target["destPath"] as String,
            target["replaceDir"] as Map<String, String>,
            target["replaceContent"] as Map<String, String>,
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
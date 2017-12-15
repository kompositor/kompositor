package top.sandwwraith.kompositor

import org.junit.Assert.assertEquals
import org.junit.Test
import java.nio.file.Paths

class PathsTest {

    @Test
    fun testMakeAbsoluteDownloadPath() {
        val root = Paths.get(".")
        val reported = Paths.get("kotlin-gradle/gradle")
        val answer = Paths.get("./gradle")
        assertEquals(answer, makeAbsoluteSavePath(root, reported))
    }
}
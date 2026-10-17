package com.nen.gatekeeper

import com.nen.gatekeeper.data.plugin.Hdhub4u
import com.nen.gatekeeper.data.repo.MediaRepository
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun testingPlugin() {
        runBlocking {
            val result = MediaRepository(Hdhub4u())
            println(result.getHome())
        }
    }
}
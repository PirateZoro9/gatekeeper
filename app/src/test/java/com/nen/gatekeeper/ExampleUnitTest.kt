package com.nen.gatekeeper

import com.nen.gatekeeper.data.plugin.FourKHDHub
import com.nen.gatekeeper.data.repo.MediaRepository
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * Unit test to verify FourKHDHub plugin against live data.
 */
class ExampleUnitTest {
    @Test
    fun testingPlugin() {
        runBlocking {
            val repo = MediaRepository(FourKHDHub())
            val homeResult = repo.getHome()
            println("Home Result: $homeResult")
            val firstCard = homeResult.getOrNull()?.firstOrNull()?.rowCards?.firstOrNull()
            println("First Movie Card: $firstCard")
            if (firstCard != null) {
                val detailsResult = repo.getDetails(firstCard.detailedUrl)
                println("Movie Details: $detailsResult")
            }
        }
    }
}

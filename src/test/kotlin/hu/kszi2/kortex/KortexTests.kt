package hu.kszi2.kortex

import hu.kszi2.kortex.KortexInterval
import hu.kszi2.kortex.kortex
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class KortexTests {

    @Test
    fun kortexScope() {
        var k: Long = 0

        runBlocking {
            kortex {
                k = this.interval.delay
            }
        }
        assertEquals("The delay values did not match", 1000L, k)
    }

    @Test
    fun returnValue() {
        val ret = runBlocking {
            kortex {
                interval = KortexInterval.SECOND
                krunonce { 3 + 2 }
            }
        }
        assertEquals("Return value did not match", 5, ret)
    }

}
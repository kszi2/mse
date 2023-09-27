package hu.kszi2.kontext

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class KontextTests {

    @Test
    fun kontextScope() {
        var k: Long = 0

        runBlocking {
            kontext {
                k = this.interval.delay
            }
        }
        assertEquals("The delay values did not match", 1000L, k)
    }

    @Test
    fun returnValue() {
        val ret = runBlocking {
            kontext {
                interval = KontextInterval.SECOND
                krunOnce { 3 + 2 }
            }
        }
        assertEquals("Return value did not match", 5, ret)
    }

}
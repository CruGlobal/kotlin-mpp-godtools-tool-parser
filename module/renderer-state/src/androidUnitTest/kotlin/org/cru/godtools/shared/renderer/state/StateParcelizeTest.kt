package org.cru.godtools.shared.renderer.state

import android.os.Parcel
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlin.test.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StateParcelizeTest {
    @Test
    fun testParcelize() {
        val orig = State()
        orig.setVar("key1", listOf("value1"))
        orig.setVar("key2", listOf("a", "b"))

        val parceledBytes = with(Parcel.obtain()) {
            writeParcelable(orig, 0)
            marshall()
        }

        val created = with(Parcel.obtain()) {
            unmarshall(parceledBytes, 0, parceledBytes.size)
            setDataPosition(0)
            readParcelable<State>(this::class.java.classLoader)!!
        }

        assertEquals(1, created.getVar("key1").size)
        assertEquals(listOf("value1"), created.getVar("key1"))
        assertEquals(orig.getVar("key2"), created.getVar("key2"))
    }
}

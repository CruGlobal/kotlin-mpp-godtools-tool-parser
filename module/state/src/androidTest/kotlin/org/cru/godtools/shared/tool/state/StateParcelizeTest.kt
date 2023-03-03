package org.cru.godtools.shared.tool.state

import android.os.Parcel
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class StateParcelizeTest {
    @Test
    fun testParcelize() {
        val orig = State()
        orig["key1"] = "value1"
        orig["key2"] = listOf("a", "b")

        val parceledBytes = with(Parcel.obtain()) {
            writeParcelable(orig, 0)
            marshall()
        }

        val created = with(Parcel.obtain()) {
            unmarshall(parceledBytes, 0, parceledBytes.size)
            setDataPosition(0)
            readParcelable<State>(this::class.java.classLoader)!!
        }

        assertEquals(1, created.getAll("key1").size)
        assertEquals(listOf("value1"), created.getAll("key1"))
        assertEquals(orig.getAll("key2"), created.getAll("key2"))
    }
}

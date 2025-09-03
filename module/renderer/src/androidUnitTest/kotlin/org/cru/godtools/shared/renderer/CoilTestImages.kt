package org.cru.godtools.shared.renderer

import android.graphics.drawable.Drawable
import coil3.asImage

object CoilTestImages {
    val bruce = Drawable.createFromStream(this.javaClass.getResourceAsStream("bruce.jpg"), "bruce.jpg")!!.asImage()
    val blackPanther =
        Drawable.createFromStream(this.javaClass.getResourceAsStream("black_panther.png"), "black_panther.png")!!
            .asImage()
    val waterfall =
        Drawable.createFromStream(this.javaClass.getResourceAsStream("waterfall.jpg"), "waterfall.jpg")!!.asImage()
}

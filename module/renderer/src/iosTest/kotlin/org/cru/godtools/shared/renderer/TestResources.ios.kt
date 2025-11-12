package org.cru.godtools.shared.renderer

import okio.FileSystem
import okio.Path.Companion.toPath
import org.ccci.gto.android.common.okio.chroot
import platform.Foundation.NSBundle

actual inline val FileSystem.Companion.RESOURCES get() = SYSTEM.chroot(NSBundle.mainBundle.bundlePath.toPath())

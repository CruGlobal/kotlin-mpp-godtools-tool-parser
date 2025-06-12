package org.cru.godtools.shared.renderer.util

import okio.FileSystem
import okio.Path.Companion.toPath
import org.ccci.gto.android.common.okio.chroot
import org.ccci.gto.android.common.okio.readOnly

@Suppress("FunctionName")
fun ResourcesFileSystem(directory: String) = FileSystem.SYSTEM.chroot(directory.toPath()).readOnly()

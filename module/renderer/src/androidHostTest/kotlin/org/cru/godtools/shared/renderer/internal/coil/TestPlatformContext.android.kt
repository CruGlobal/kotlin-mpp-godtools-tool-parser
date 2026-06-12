package org.cru.godtools.shared.renderer.internal.coil

import androidx.test.core.app.ApplicationProvider
import coil3.PlatformContext

actual val TestPlatformContext: PlatformContext get() = ApplicationProvider.getApplicationContext()

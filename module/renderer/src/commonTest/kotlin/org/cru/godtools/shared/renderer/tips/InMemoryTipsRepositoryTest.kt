package org.cru.godtools.shared.renderer.tips

class InMemoryTipsRepositoryTest : TipsRepositoryTest() {
    override val repository = InMemoryTipsRepository()
}

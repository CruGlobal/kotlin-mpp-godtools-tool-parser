package org.cru.godtools.shared.tool.parser.model

import org.cru.godtools.shared.tool.parser.model.lesson.LessonPage
import org.cru.godtools.shared.tool.parser.model.tract.TractPage

@Deprecated(
    "Since v0.4.0, use pages instead which will support different page types in the future.",
    ReplaceWith("pages.filterIsInstance<LessonPage>()", "org.cru.godtools.shared.tool.parser.model.lesson.LessonPage")
)
val Manifest.lessonPages get() = pages.filterIsInstance<LessonPage>()
@Deprecated(
    "Since v0.4.0, use pages instead which will support different page types in the future.",
    ReplaceWith("pages.filterIsInstance<TractPage>()", "org.cru.godtools.shared.tool.parser.model.tract.TractPage")
)
val Manifest.tractPages get() = pages.filterIsInstance<TractPage>()

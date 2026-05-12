# Dismissed Review Issues

Issues listed here are suppressed in future PR reviews.

---

## Manifest.File missing @JsExport
**Pattern**: Flagging a nested interface/class inside a `@JsExport` class for missing `@JsExport` when it is already present in the generated TypeScript headers without the annotation
**Reason**: Kotlin's JS compiler includes nested types of `@JsExport` classes in the TypeScript output without requiring the annotation on the nested type itself
**Dismissed**: 2026-05-12
**Dismissed by**: Daniel Frett

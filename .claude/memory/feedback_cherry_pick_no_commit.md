---
name: cherry-pick with --no-commit flag
description: Use --no-commit when cherry-picking to stage changes without creating a commit
type: feedback
---

Use `git cherry-pick --no-commit <hash>` (or `-n`) to stage the cherry-picked changes without creating a commit, so they can be immediately amended into another commit.

**Why:** Avoids the extra soft-reset step needed to undo the cherry-pick commit before amending.

**How to apply:** Any time cherry-picking changes in order to fold them into an existing commit via `--amend` or fixup rebase.
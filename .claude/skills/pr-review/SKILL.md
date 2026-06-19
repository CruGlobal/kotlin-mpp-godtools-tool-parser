---
name: pr-review
description: Review a pull request against godtools-shared project conventions. Use when asked to review a PR, check code quality, or audit changes.
argument-hint: [pr-number]
allowed-tools: Bash, Read, Grep, Glob, Write, Edit
---

Review pull request $ARGUMENTS against the godtools-shared project conventions.

## Steps

1. Check for dismissed issues by reading `.claude/skills/pr-review/dismissed-issues.md` if it exists.
   Load all dismissed entries — each has a **Pattern** and **Reason**. You will use these to suppress matching findings later.

2. Fetch the PR diff and metadata. If `$ARGUMENTS` is provided, use it as the PR number:
```
gh pr diff $ARGUMENTS
gh pr view $ARGUMENTS
```
If no PR number is given (or the above fails because no upstream PR exists), fall back to reviewing the current branch against `main`:
```
git diff main...HEAD
git log main...HEAD --oneline
```
Use the branch name and commit log as the "title" in the review header.

3. Identify all changed files and categorize them (module, source set, tests, build config, etc.).

4. Pre-flight checks — run ktlint and lint, recording results for the review:
```
./gradlew :build-logic:ktlintCheck ktlintCheck
./gradlew lint
```
Any failures are reported as **❌ Must Fix** items in the review output. They do not stop the rest of the review.

5. Review each category using the checklist below. Before outputting, cross-reference every finding against dismissed patterns — a finding matches when it describes the same class of issue (not necessarily the exact file/line). Move matched findings to a separate suppressed list.

6. Output a structured review (format below).

7. Post inline comments to the PR for every ⚠️ and ❌ finding that references a specific file and line number. **Skip this step entirely when reviewing a branch with no PR — there is nowhere to post.** Otherwise, before posting, deduplicate against all existing comments (resolved or not) to avoid re-posting anything already raised:

```bash
# Get the head SHA and repo
HEAD_SHA=$(gh pr view $ARGUMENTS --json headRefOid -q .headRefOid)
REPO=$(gh repo view --json nameWithOwner -q .nameWithOwner)

# Fetch all existing review comments (resolved and unresolved) to deduplicate against
gh api repos/$REPO/pulls/$ARGUMENTS/comments --jq '[.[] | select(.in_reply_to_id == null) | {path, line, body}]'
```

For each finding, check whether any comment from the output above already covers the same file + line (or contains substantially the same text). Skip any finding that is already covered. Then bundle the remaining new comments into a single review submission:

```bash
gh api repos/$REPO/pulls/$ARGUMENTS/reviews \
  --method POST \
  --field commit_id="$HEAD_SHA" \
  --field event="COMMENT" \
  --field "comments[][path]=<file path>" \
  --field "comments[][line]=<line number>" \
  --field "comments[][side]=RIGHT" \
  --field "comments[][body]=<finding text>

🤖 Posted by [Claude Code](https://claude.ai/code)" \
  # repeat --field "comments[]..." for each new finding
```

Use the exact file path from the diff (e.g. `module/parser/src/.../Foo.kt`) and the line number in the current version of the file (RIGHT side). Each comment body should contain the full finding description. Always append the attribution footer `\n\n🤖 Posted by [Claude Code](https://claude.ai/code)` to each comment. If no new actionable findings exist (only ✅ items or all already commented), skip this step.

8. If the review has **no ❌ or ⚠️ findings** (only ✅ and/or ⏭️ items), ask the user whether to post the full review. **Skip this step entirely when reviewing a branch with no PR — branch review mode is local-only.** Otherwise, if they say yes:
   - Check whether the PR author matches the current git user (`gh pr view $ARGUMENTS --json author -q .author.login` vs `gh api user -q .login`)
   - If it is a **self-review**, post with `--comment` (GitHub does not allow self-approval)
   - If it is **someone else's PR**, ask whether to approve or just comment, then post with `--approve` or `--comment` accordingly
   - Always append `\n\n🤖 Posted by [Claude Code](https://claude.ai/code)` to the body

9. After the review output, print:

```
---
To dismiss a finding so it won't appear in future reviews, say:
  dismiss: <short title> — <reason>
```

---

## Review Checklist

### Multiplatform Source Sets

- [ ] Platform-specific APIs (Android, iOS, JS) are NOT used in `commonMain` — only in the appropriate platform source set
- [ ] New platform-specific functionality is placed in the correct source set (`androidMain`, `iosMain`, `jsMain`, or `commonMain`)
- [ ] `expect`/`actual` declarations are used (not copy-pasted implementations) when platform differences are needed
- [ ] New files in `commonMain` use only Kotlin stdlib and declared multiplatform dependencies

### Parser Module (`module/parser`)

- [ ] New model types are `data class` or `sealed class` where appropriate
- [ ] Parser results use the sealed `ParserResult` hierarchy: `ParserResult.Data` or `ParserResult.Error` (never throw)
- [ ] Parsing is done via `XmlPullParser` (the multiplatform abstraction) — never Android's or platform XML APIs directly
- [ ] New public API surface uses `internal` where exposure is not intentional

### Renderer Module (`module/renderer`)

- [ ] New Compose components live in `renderer/content/` for content types or appropriate subdirectory
- [ ] `@Composable` functions contain no business logic — pure rendering only
- [ ] Bare `launch { }` calls in a `@Composable` body re-run on every recomposition — use `LaunchedEffect` for one-shot effects keyed by inputs, or only call `scope.launch { }` from event callbacks (e.g. `onClick`, `eventSink` lambdas)
- [ ] Writes that must survive composition cancellation use `launch(start = CoroutineStart.UNDISPATCHED) { withContext(NonCancellable) { … } }` — `rememberCoroutineScope()` is canceled when the composable leaves composition; `UNDISPATCHED` ensures the coroutine starts before the first suspension and `NonCancellable` ensures the write completes. Do NOT use `launch(NonCancellable) { … }` — passing `NonCancellable` to `launch` replaces the parent `Job`, breaking structured concurrency
- [ ] `LocalInspectionMode` is not set to `true` in production code (only in tests/previews)

### Renderer State Module (`module/renderer-state`)

- [ ] New events are added to the appropriate sealed interface (not as ad-hoc callbacks)
- [ ] State changes flow through `MutableSharedFlow` — not direct mutation
- [ ] `ExpressionContext` implementations correctly delegate variable lookups

### Module Build Files

For any new module, check `build.gradle.kts`:
- [ ] Applies `godtools-shared.module-conventions` convention plugin
- [ ] Android targets come from `com.android.kotlin.multiplatform.library` (applied by the convention plugin) — do not apply the standalone `com.android.library` plugin
- [ ] No manual duplication of multiplatform target setup (handled by convention plugin)
- [ ] No redundant Kover or Ktlint config (handled by convention plugin)
- [ ] Additional plugins (KSP, ANTLR, Parcelize, Paparazzi) are only added when the module actually needs them
- [ ] Custom Maven repos (CruGlobal JFrog for ANTLR-Kotlin and material-color-utilities, JitPack, Deezer) are configured only in the root `settings.gradle.kts` — modules must not hardcode `repositories { maven { url = ... } }` blocks

### Testing

**Paparazzi snapshot tests (renderer)**
- [ ] New visual components have a corresponding Paparazzi snapshot test
- [ ] Test class extends `BasePaparazziTest`
- [ ] Uses `contentSnapshot { … }` for static renders
- [ ] Uses `animatedContentSnapshot { … }` for animated content (Lottie, etc.) with appropriate `start`/`end` range
- [ ] `maxPercentDifference = 0.0` is not overridden without justification

**Unit tests (all modules)**
- [ ] Flow-based tests use Turbine (`flow.test { … }`)
- [ ] Parser tests use in-memory XML strings or test fixture files — never real network
- [ ] Tests are in the correct source set (`commonTest`, `androidHostTest`, etc.)

### Code Style

Ktlint and `.editorconfig` enforce most style rules (line length, formatter rules, constant naming) — step 4's pre-flight already covers those. Manual checks:

- [ ] Package prefix: `org.cru.godtools.shared`

### General Quality

- [ ] No Android framework references (`Context`, `Activity`, etc.) in `commonMain` code
- [ ] No hardcoded strings that should come from the parsed manifest
- [ ] No new Gradle dependencies added without a corresponding entry in `gradle/libs.versions.toml`
- [ ] Dependencies added to `libs.versions.toml` follow existing naming conventions
- [ ] Logging uses Kermit (`Logger.*`) — no `println` or Android `Log.*` calls

### JS Export Surface

- [ ] No new usage of `@KustomExport` or `deezer.kustomexport:*` dependencies — the library is being phased out (existing usages are not a finding; new ones are ⚠️)

### Deprecated API Usage

Scan changed files for deprecated API calls. Flag each one as a **Minor Issue** (⚠️) with a suggested replacement.

To surface deprecated usages introduced or touched in the diff:
```bash
# Compiler warnings will flag @Deprecated-annotated symbols
./gradlew testAndroidHostTest 2>&1 | grep -i "deprecat"
```

### PR Hygiene

- [ ] No unrelated auto-formatter whitespace changes mixed into the diff (check with `git diff main...HEAD --stat` — flag files with churn that don't match the stated PR scope)

---

## Output Format

Structure the review as (use `## PR Review: <title> (#<number>)` when reviewing a PR, or `## Review: <branch-name>` when reviewing a branch locally):

```
## PR Review: <title> (#<number>)

### Summary
<1–2 sentence summary of what the PR does>

### Checklist Findings

#### ✅ Looks Good
- <item>

#### ⚠️ Minor Issues
- <file:line> — <issue> — <suggested fix>

#### ❌ Must Fix
- <file:line> — <issue> — <suggested fix>

#### ⏭️ Suppressed
- <short title> — dismissed: <reason>
(omit this section entirely if nothing was suppressed)

### Overall Verdict
APPROVE / REQUEST CHANGES / COMMENT
<brief rationale>
```

Be specific. Reference file paths and line numbers. Cite the relevant convention from CLAUDE.md when flagging an issue.

---

## Handling Dismissals

When the user says `dismiss: <title> — <reason>` (in any form — "dismiss the X issue because Y", etc.):

1. Read `.claude/skills/pr-review/dismissed-issues.md` if it exists (create it if not).
2. Run `git config user.name` to get the current user's name.
3. Append a new entry in this format:

```markdown
## <title>
**Pattern**: <describe the class of issue broadly enough to match future occurrences>
**Reason**: <reason the user gave>
**Dismissed**: <today's date as YYYY-MM-DD>
**Dismissed by**: <git user.name>
```

4. If the current session reviewed a PR, find any open (unresolved) comment thread on that PR matching the dismissed issue. Use the GraphQL API to locate threads and resolve the matching one, replying with the dismissal reason first:

```bash
REPO=$(gh repo view --json nameWithOwner -q .nameWithOwner)
OWNER=${REPO%%/*}
REPONAME=${REPO##*/}

# Find unresolved review threads
gh api graphql -f query="
{
  repository(owner: \"$OWNER\", name: \"$REPONAME\") {
    pullRequest(number: $PR_NUMBER) {
      reviewThreads(first: 100) {
        nodes {
          id
          isResolved
          comments(first: 1) {
            nodes { id body path line }
          }
        }
      }
    }
  }
}"
```

Match the thread by file path, line number, or substantial text overlap with the dismissed finding. Then reply to the thread and resolve it:

```bash
# Reply to the thread's first comment explaining the dismissal
gh api repos/$REPO/pulls/$PR_NUMBER/comments \
  --method POST \
  --field in_reply_to=<comment_id> \
  --field body="Dismissed: <reason given by user>

🤖 [Claude Code](https://claude.ai/code)"

# Resolve the thread via GraphQL
gh api graphql -f query="
mutation {
  resolveReviewThread(input: { threadId: \"<thread_node_id>\" }) {
    thread { id isResolved }
  }
}"
```

5. Confirm to the user what was added and that it will be suppressed in future reviews.

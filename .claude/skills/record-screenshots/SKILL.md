---
name: record-screenshots
description: Trigger the Record Snapshots CI workflow and integrate the resulting snapshot images into the appropriate Paparazzi commit on the current branch.
allowed-tools: Bash, Read, Grep, Glob
---

Record updated Paparazzi snapshots for the current branch and fold the resulting CI commit into the right local commit.

## Steps

### 1. Capture branch state

```bash
git branch --show-current
git log --oneline main..HEAD
```

Note the current branch name and list of commits since `main`.

### 2. Push to origin

```bash
git push origin HEAD
```

If this fails (rejected), explain why and stop — do not force-push without user confirmation.

### 3. Trigger the workflow

```bash
gh workflow run "Record Snapshots" --ref <branch>
```

Wait a few seconds for GitHub to register the run, then get its ID:

```bash
sleep 5
gh run list --workflow="Record Snapshots" --branch=<branch> --limit=1 --json databaseId,status,conclusion
```

### 4. Wait for completion

```bash
gh run watch <run-id> --interval 30
```

If the conclusion is not `success`, fetch and display the run log and stop:

```bash
gh run view <run-id> --log-failed
```

### 5. Fetch the screenshot commit

```bash
git fetch origin <branch>
```

Verify the remote is exactly one commit ahead (the screenshot commit):

```bash
git log --oneline HEAD..origin/<branch>
```

If there is more than one new commit, warn the user and stop — the situation is unexpected and needs manual handling.

Check the screenshot commit message. If it says "No changed snapshots", inform the user that no snapshots changed and stop — there is nothing to fold in.

### 6. Find the target commit

Look for the most recent commit on the branch (i.e. in `main..HEAD`) that touched Paparazzi test sources or related composables. Check in this order:

a. Commits that modified `*Paparazzi*.kt` files:
```bash
git log --oneline main..HEAD -- '*Paparazzi*.kt'
```

b. If none found, commits that modified Composable UI source files:
```bash
git log --oneline main..HEAD -- '*/src/commonMain/kotlin/*.kt'
```

c. If still none found, commits that modified any test source file:
```bash
git log --oneline main..HEAD -- '*/src/androidUnitTest/*' '*/src/commonTest/*'
```

If there are multiple candidates at any step, show them and ask the user which commit the screenshots belong to before proceeding.

### 7. Apply the snapshot changes locally

Stage only the snapshot files from the remote commit into the working tree:

```bash
# Get the list of files changed by the screenshot commit
git diff --name-only HEAD origin/<branch>

# Stage those files at the remote version
git checkout origin/<branch> -- $(git diff --name-only HEAD origin/<branch>)
```

### 8. Create a fixup commit and autosquash

```bash
git commit --fixup=<target-sha>
N=$(git rev-list --count main..HEAD)
GIT_SEQUENCE_EDITOR=true git rebase -i --autosquash HEAD~$((N + 1))
```

If the rebase fails (conflict), explain the conflict and stop — do not attempt to auto-resolve.

### 9. Confirm and force-push

Show the user the resulting log:

```bash
git log --oneline main..HEAD
```

**Ask for explicit confirmation before force-pushing.** Then, with confirmation:

```bash
git push --force-with-lease origin <branch>
```
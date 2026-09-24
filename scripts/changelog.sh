#!/usr/bin/env bash
#
# Prints release notes as Markdown, built from the Conventional Commits since
# the previous stable release.
#
#   scripts/changelog.sh v1.3.0     notes for an existing tag (what the Release workflow does)
#   scripts/changelog.sh HEAD       preview: what the next release would contain
#
# Commits that don't follow Conventional Commits are still listed, in their own
# section, so they're visible in the release.
set -euo pipefail

die() {
  echo "error: $*" >&2
  exit 1
}

[[ $# -eq 1 ]] || { echo "usage: changelog.sh <tag or commit>" >&2; exit 2; }
ref="$1"
git rev-parse --quiet --verify "$ref^{commit}" >/dev/null || die "unknown tag or commit '$ref'"

# The types of @commitlint/config-conventional, lower-case as the linter requires.
# Groups: 1 type, 3 scope, 4 "!", 5 description.
readonly HEADER='^(build|chore|ci|docs|feat|fix|perf|refactor|revert|style|test)(\(([^)]+)\))?(!)?: (.+)$'

# The previous release is the nearest stable tag in this commit's history, not the
# highest version: that's what the notes are "since". Betas are skipped so a release
# covers everything since the last stable one, and $ref itself is excluded.
prev=$(git describe --tags --abbrev=0 --match 'v*' --exclude '*-*' --exclude "$ref" "$ref" 2>/dev/null || true)
range="${prev:+$prev..}$ref"   # no previous release: the whole history

breaking="" features="" fixes="" other="" unconventional=""
while IFS= read -r sha; do
  subject=$(git log -1 --format=%s "$sha")
  if [[ "$subject" =~ $HEADER ]]; then
    type="${BASH_REMATCH[1]}" scope="${BASH_REMATCH[3]}" bang="${BASH_REMATCH[4]}" description="${BASH_REMATCH[5]}"
    if [[ -n "$bang" ]] || git log -1 --format=%b "$sha" | grep -qE '^BREAKING[ -]CHANGE:'; then
      breaking+="- $subject"$'\n'
    elif [[ "$type" == feat ]]; then
      features+="- ${scope:+**$scope:** }$description"$'\n'
    elif [[ "$type" == fix ]]; then
      fixes+="- ${scope:+**$scope:** }$description"$'\n'
    else
      other+="- $subject"$'\n'
    fi
  elif [[ "$subject" == 'Revert "'* ]]; then   # git's own revert message, accepted by the linter
    other+="- $subject"$'\n'
  else
    unconventional+="- $subject"$'\n'
  fi
done < <(git log --no-merges --format=%H "$range")

section() {
  [[ -z "$2" ]] || printf '## %s\n\n%s\n' "$1" "$2"
}

if [[ -z "$breaking$features$fixes$other$unconventional" ]]; then
  echo "No changes since ${prev:-the first commit}."
  exit 0
fi
section "⚠ Breaking changes" "$breaking"
section "Features" "$features"
section "Bug fixes" "$fixes"
section "Other changes" "$other"
section "Not following Conventional Commits" "$unconventional"

# In GitHub Actions, link the full diff.
if [[ -n "$prev" && -n "${GITHUB_REPOSITORY:-}" ]]; then
  echo "**Full changelog:** ${GITHUB_SERVER_URL:-https://github.com}/$GITHUB_REPOSITORY/compare/$prev...$ref"
fi

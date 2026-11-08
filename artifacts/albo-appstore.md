# Albo — Reverse Engineering Project Brief

## Target application

- Name: Albo: Save & Organize
- App Store URL: https://apps.apple.com/us/app/albo-save-organize/id6578421992
- App Store ID: 6578421992
- Developer: The Feel Good Project Ltd
- Developer website: https://www.thefeelgoodproject.co/
- Privacy policy: https://www.albo.inc/privacy-policy
- Minimum iOS version: 17.0
- Listed size: 312.9 MB
- Bundle identifier: Unknown; must be verified
- API endpoints: Unknown; must be investigated

## Objective

Build an independent, open-source Android alternative using observable app behavior and independently implemented code.

## Publicly documented functionality

1. Save links, videos, recipes, books, workouts, tools, and places.
2. Import content from other apps.
3. Organize content into categories and custom collections.
4. Search saved content.
5. Automatically classify and enrich saved content.
6. Display saved places on a map.
7. Plan trips using saved places.
8. Organize recipes, meal plans, and groceries.
9. Share collections and collaborate with friends.

## Investigation requirements

1. Inspect available tools and artifacts before modifying code.
2. Verify the bundle identifier if possible.
3. Examine the App Store screenshots and document the main screens and navigation.
4. Identify Android equivalents for iOS share-sheet and device integrations.
5. Determine how link previews and content extraction can be implemented using independent data sources.
6. Investigate public documentation and existing open-source alternatives.
7. Identify features requiring a backend, external APIs, authentication, or AI.
8. Separate verified facts, observations, and hypotheses.
9. Produce a prioritized implementation plan for an Android MVP.
10. Do not invent API endpoints, internal architecture, or unavailable source code.

## Initial implementation priority

Start with:

- Android share intent handling
- URL parsing and metadata extraction
- Local persistence
- Save detail screen
- Search and custom collections

Defer advanced AI classification, social collaboration, trip planning, and subscription features until the core workflow is functional.

Do not modify the existing repository until the investigation report is complete.

## Agent Working Rules — Minimal Tokens, Maximum Progress

### Core principle

Think like a pragmatic, lazy senior developer. Solve the problem with the least code, lowest complexity, and fewest agent interactions possible.

### Token and context efficiency

- Keep responses short. Report only decisions, findings, blockers, and the next action.
- Never repeat the project brief or summarize information already documented.
- Inspect only files relevant to the current task. Avoid dumping entire files or directory trees.
- Prefer targeted searches and small code excerpts over broad exploration.
- Do not generate lengthy plans, reports, or explanations unless explicitly required.
- Reuse existing dependencies, utilities, patterns, and components.
- Avoid redundant tool calls and repeated investigations.
- Maintain a concise `artifacts/progress.md` with verified discoveries, completed work, blockers, and the next step. Update it only when meaningful information changes.

### Coding rules

- Implement the smallest complete solution that satisfies the current requirement.
- Prefer simple, readable code over abstractions, frameworks, factories, and unnecessary design patterns.
- Do not overengineer, prematurely optimize, or build speculative features.
- Do not add dependencies when existing tools can solve the problem.
- Do not refactor unrelated code or modify working functionality without a clear reason.
- Do not create unnecessary files, documentation, tests, or configuration.
- Preserve existing project conventions.
- Avoid duplicate state, duplicate logic, and unnecessary wrappers.
- Implement one feature or meaningful vertical slice at a time.

### Execution rules

- Inspect before changing. Make the smallest targeted change.
- Run only the checks relevant to that change.
- Fix errors caused by your changes; report unrelated failures briefly.
- Do not repeatedly retry a failing command without changing the diagnosis.
- Never claim success without checking the result.
- Ask for approval before destructive actions, expensive operations, or changes outside the project scope.
- If blocked, state the exact blocker and the minimum action needed to proceed.

### Investigation rules

- Separate verified facts from assumptions.
- Do not invent app behavior, API endpoints, bundle identifiers, or internal architecture.
- Prefer the cheapest reliable investigation method first.
- Investigate only what is necessary to implement the current feature.
- Do not analyze the entire IPA or repository when a targeted inspection is sufficient.
- If evidence is unavailable, record the uncertainty and continue with independent, testable functionality where possible.

### Response format

After each task, report only:

1. **Done:** What changed.
2. **Check:** Whether the relevant validation passed.
3. **Next:** One necessary next step, only if required.

Skip irrelevant sections. No motivational text, generic advice, long recaps, or unsolicited feature suggestions.

### Project execution strategy

Work in small, independently testable increments. Prioritize a functional Android MVP over complete feature parity with Albo.

Do not implement advanced features until the core save → organize → retrieve workflow works.

When requirements are ambiguous, choose the simplest reversible assumption if safe. Ask a question only when proceeding would risk substantial rework or an unsafe action.

**Default behavior: inspect narrowly, implement minimally, validate, report concisely, stop.**

## Documentation, Decisions, App Flow, and Maintainability

### 1. Decision log — `artifacts/decisions.md`

- Record meaningful technical or product decisions where choosing X over Y has lasting consequences.
- For each decision, briefly document:
  - **Decision:** What was chosen.
  - **Alternatives:** The realistic options considered.
  - **Reason:** Why the chosen option is better for this project.
  - **Trade-off:** The main downside or limitation.
- Record decisions such as framework selection, state management, local database, API strategy, authentication, and significant dependency choices.
- Do not log trivial implementation details, routine syntax choices, or obvious decisions.
- Add entries when decisions are made, not retrospectively after every task.
- If a decision changes, update its status and record why. Preserve the history of meaningful decisions.
- Keep entries concise and evidence-based. Never invent alternatives or claim comparisons that were not performed.

### 2. App flow — `artifacts/app-flow.md`

- Maintain a concise, accurate description of the app's screens and navigation.
- Document:
  - Screen name and purpose.
  - How the user enters and exits the screen.
  - Important user actions and resulting transitions.
  - Loading, empty, error, and success states where relevant.
  - Authentication or permission requirements where applicable.
- Include the core user journey from receiving a shared URL to saving, organizing, and retrieving content.
- Use a Mermaid flowchart when it makes the flow easier to understand. Prefer one small diagram over lengthy prose.
- Update the flow whenever implemented behavior changes.
- Clearly distinguish observed behavior from proposed behavior and implemented behavior.
- Do not document unverified Albo behavior as fact.

### 3. Code comments — optimize for the next developer

- Add comments where they materially improve debugging, maintenance, or understanding.
- Explain **why** code exists, not what the code visibly does.
- Comment non-obvious business rules, platform-specific workarounds, tricky state transitions, parsing edge cases, API assumptions, and failure handling.
- Document important invariants and surprising side effects.
- Explain why a seemingly simpler approach was rejected when the reason would not be obvious from the code.
- For complex functions, briefly document purpose, important inputs, outputs, and failure conditions.
- Use short, local comments near the relevant code.
- Avoid comments that merely restate the code, excessive inline narration, redundant docstrings, and speculative explanations.
- Keep comments synchronized with implementation. Remove or correct misleading comments when code changes.
- Never use comments as a substitute for clear naming, simple logic, or proper error handling.
- Add useful logs for diagnosable failures where appropriate, but never log credentials, tokens, private URLs, or sensitive user data.

### 4. Documentation consistency

- `artifacts/albo-appstore.md` is the initial project brief and source of investigation scope.
- `artifacts/decisions.md` records meaningful decisions and their rationale.
- `artifacts/app-flow.md` records the current and planned user flows.
- `artifacts/progress.md` records current status, verified findings, blockers, and the next step.
- Keep these files concise. Do not copy the same information across multiple files.
- Update only the documentation affected by a change.
- Do not create additional documentation files unless they solve a concrete maintenance problem.

### 5. Completion criteria

Before declaring a feature complete:

1. Verify its relevant behavior.
2. Run appropriate targeted checks.
3. Update the app flow if user-visible navigation or behavior changed.
4. Update the decision log if a meaningful architectural or product decision was made.
5. Add or correct comments where the implementation contains non-obvious logic.
6. Update progress with the actual result and next step.

Do not make documentation updates merely to create activity. Every entry must help a future developer understand, debug, or extend the project.

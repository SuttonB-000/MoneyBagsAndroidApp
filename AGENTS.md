AGENTS.md
Project Overview
- Application: Money Bags - Money Manager
- Platform: Android
- Course: SDEV264 - Final Project
- Known version: 0.1.0 (verify against the current build configuration)
Money Bags is a visual budgeting application that allows users to organize money into individual budgeting categories represented by money bags. Rather than relying primarily on charts and graphs, the application provides a simple visual representation of category balances and allows users to track spending and add funds directly.
Development Priorities
- Prioritize simplicity, usability, readability, and maintainability.
- Keep implementations appropriate for a college software development project.
- Prefer stable Android APIs and existing project conventions over unnecessary dependencies, experimental APIs, or complex abstractions.
- Make focused changes that address the requested feature without refactoring unrelated code.
- Preserve existing functionality unless a change is explicitly requested.
- Explain significant changes, their purpose, and any new dependencies or configuration requirements so the developer can understand and maintain the implementation.
- Do not claim that a build or test passed unless it was actually executed successfully.
Existing Functionality
The following behaviors have been established during project development. Verify them against the current source before making changes.
Home Screen / Main Activity
- Display budgeting categories as money bag icons with their associated names and balances.
- Long-press empty space to open the Add a New Money Bag dialog.
- Allow the user to specify a bag name and starting amount when creating a bag.
- Long-press an existing bag to open its edit dialog.
- Allow users to edit a bag's name and balance or delete the bag.
- Require confirmation before deleting a bag.
- Use the +$ button to initiate adding money to a bag.
- Use the -$ button to initiate tracking spending from a bag.
- Present contextual transaction instructions above the bottom blue bar.
Settings / Secondary Activity
- Support the existing light and dark theme options.
- Allow users to select a currency and reflect the selected currency on the home screen and relevant monetary displays.
- Include an About section displaying the application version.
- Include a Help button that opens instructions organized into Money Bags and Track Spending sections.
- Ensure settings and dialogs remain readable under both themes, including Help and Select Currency.
UI and Design Guidelines
- Maintain the money bag visual metaphor as the central feature of the application.
- Use the existing color palette and theme definitions. Do not introduce unrelated colors or hard-coded theme overrides.
- Preserve the established blue header and bottom bar styling unless requested otherwise.
- Use the existing DM Sans typography and established title emphasis.
- Keep the +$ and -$ labels consistent with the existing interface.
- Ensure labels, placeholders, dialog text, buttons, and icons have sufficient contrast in light and dark modes.
- Preserve the established long-press interactions and provide discoverable instructions in Help.
- Reuse existing drawable assets, including the money bag artwork, and follow Android resource naming conventions.
- Keep layouts usable on different Android screen sizes and avoid unnecessary visual clutter.
Implementation Guidelines
- Inspect the repository structure, build configuration, README, and relevant source files before changing code.
- Follow the existing language, UI framework, state management, navigation, persistence, and package structure. Do not introduce a new architecture without approval.
- Reuse existing components and state-handling logic where practical.
- Keep UI state and monetary values consistent after creating, editing, deleting, or updating a bag.
- Validate monetary input before applying changes; handle blank and invalid values without crashing. Do not change existing policies on negative balances or zero-value transactions without discussing them.
- Keep currency formatting consistent with the selected currency. Do not silently introduce exchange-rate conversion or alter stored amounts when changing display currency.
- Preserve existing persistence behavior; do not replace storage or introduce destructive migrations without approval.
- Prefer centralized theme values and avoid duplicating formatting or business logic.
- Keep Gradle changes minimal. Verify compatibility with the current Android Gradle Plugin, SDK, and dependencies before changing configuration or enabling build features.
- Avoid experimental UI APIs when a stable alternative is available.
- When moving files or assets, update affected references and imports and confirm that resources still resolve.
Validation and Testing
Use the repository's Gradle wrapper and existing test infrastructure. From the repository root, typical Android validation commands are:
./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew lintDebug
On Windows, use gradlew.bat instead of ./gradlew. Confirm that these tasks exist in the current project; module names and build variants may differ. Run instrumented tests through the existing configuration when an emulator or Android device is available.

For relevant changes, manually verify:
- [ ] Application launches and the home screen displays correctly.
- [ ] A new money bag can be created using a long press on empty space.
- [ ] Existing bags can be edited and deleted with confirmation; cancellation preserves the bag.
- [ ] Adding money and tracking spending update the correct balance.
- [ ] Invalid input is handled without crashing or unintentionally changing balances.
- [ ] Home and Settings navigation works correctly.
- [ ] Light and dark modes display readable labels, dialogs, and buttons.
- [ ] Currency changes appear consistently on relevant screens.
- [ ] Help instructions match actual controls and behavior.
- [ ] The About section displays the intended version.
- [ ] Existing state and persistence behavior have not regressed.
- [ ] Resource references resolve after moving or renaming files.
Report validation results and any checks that could not be performed.
Documentation
- Maintain the README.md as the primary user-facing project documentation.
- Keep the Overview, Features, How to Use, and Built With sections accurate.
- Document implemented functionality rather than presenting planned features as complete.
- Keep explanations accessible to someone learning Android development.
- Use meaningful comments for non-obvious logic rather than commenting every line.
- Respect the developer's preference to write course documentation themselves; provide explanations, important callouts, and suggested improvements when requested.
- Verify changes against the actual SDEV264 assignment requirements when those requirements are available. Do not invent grading criteria.
- Update this file when significant project conventions or functionality change.
Change Management
- Do not remove existing features, rename packages, restructure the application, or change the application ID without explicit approval.
- Do not change the application version, SDK targets, dependencies, or build settings unnecessarily.
- Do not overwrite unrelated developer changes or commit, push, or publish without authorization.
- Do not commit credentials, signing keys, local configuration, or generated build outputs.
- After implementation, summarize changed files, behavior changes, validation results, and unresolved issues or assumptions.
Scope and Verification Notes
This guidance is based on the project's established development discussions. The repository was not available for inspection when this file was created. Verify the exact package names, module layout, programming language, UI framework, storage implementation, SDK levels, dependency versions, build tasks, and assignment requirements against the current repository before relying on them. The source code and explicit developer instructions take precedence over outdated descriptions in this file.

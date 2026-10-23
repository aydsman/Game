# VS Code environment

Use JDK 21 and Microsoft's Extension Pack for Java. Open this project folder in VS Code. Press F5 and select **Run Game** to compile and debug, or Ctrl+Shift+B to compile all sources. Run the **test** task for the existing combat suite. From PowerShell, run `./scripts/build.ps1 -Test`.

The build targets Java 17 bytecode. Assets and config load relative to the project root. Player saves in `data/` and compiled files remain local.

# Branch workflow

`master` is preserved as the original baseline. `main` starts at that same committed baseline. Current game development and workspace setup live on `development`.

Work and commit on `development`. Publish with `git push -u origin development`. Publish the baseline main once with `git push -u origin main`. When testing is done, open a pull request from development into main and review CI before merging.

GitHub Actions compiles and runs the tests on Windows and Linux for pushes and pull requests targeting main/development. The workflow first reaches main when the development pull request is merged.

## Current build blocker

At environment setup, `src/combat/Ranged.java` and `src/entity/Boss.java` were empty. Recover their latest contents before expecting compilation or CI to pass. The setup preserves these files rather than replacing them with older committed versions.

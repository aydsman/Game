param([switch]$Test)
$ErrorActionPreference = 'Stop'
Push-Location (Split-Path $PSScriptRoot -Parent)
try {
    New-Item -ItemType Directory -Force -Path out | Out-Null
    $gameSources = @(Get-ChildItem -LiteralPath src -Recurse -Filter '*.java' | ForEach-Object { $_.FullName })
    $emptySources = @(Get-ChildItem -LiteralPath src -Recurse -Filter '*.java' | Where-Object Length -eq 0)
    if ($emptySources.Count -gt 0) {
        throw ('Empty Java source files must be recovered before building: ' + ($emptySources.FullName -join ', '))
    }
    & javac --release 17 -encoding UTF-8 -d out @gameSources
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
    if ($Test) {
        New-Item -ItemType Directory -Force -Path out-test | Out-Null
        $testSources = @(Get-ChildItem -LiteralPath test -Recurse -Filter '*.java' | ForEach-Object { $_.FullName })
        & javac --release 17 -encoding UTF-8 -cp out -d out-test @testSources
        if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
        $testClasspath = 'out' + [IO.Path]::PathSeparator + 'out-test'
        & java '-Djava.awt.headless=true' -cp $testClasspath TestRunner
        if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
    }
} finally {
    Pop-Location
}

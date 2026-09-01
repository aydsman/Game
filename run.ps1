Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

New-Item -ItemType Directory -Force -Path out | Out-Null
Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName } | Set-Content sources.txt
javac -d out "@sources.txt"
java -cp out Main

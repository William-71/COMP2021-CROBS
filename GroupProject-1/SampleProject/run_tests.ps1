$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$src = Join-Path $root "src"
$test = Join-Path $root "test"
$out = Join-Path $root "out_test"
$lib = Join-Path $root "lib"

if (Test-Path $out) { Remove-Item -Recurse -Force $out }
New-Item -ItemType Directory -Path $out | Out-Null

$jars = @(
  "junit-jupiter-api-5.14.0.jar",
  "junit-jupiter-engine-5.14.0.jar",
  "junit-platform-engine-1.14.0.jar",
  "junit-platform-commons-1.14.0.jar",
  "opentest4j-1.3.0.jar",
  "apiguardian-api-1.1.2.jar",
  "junit-jupiter-5.14.0.jar",
  "junit-jupiter-params-5.14.0.jar"
) | ForEach-Object { Join-Path $lib $_ }

# Need junit-platform-console for CLI if available; otherwise use Engine via reflection launcher
$cp = ($jars + $out) -join ";"

Write-Host "Compiling sources..."
$srcFiles = Get-ChildItem -Path $src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
& javac -encoding UTF-8 -source 8 -target 8 -d $out $srcFiles
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "Compiling tests..."
$testFiles = Get-ChildItem -Path $test -Recurse -Filter *.java | ForEach-Object { $_.FullName }
& javac -encoding UTF-8 -source 8 -target 8 -cp $cp -d $out $testFiles
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

# Download console launcher if missing
$consoleJar = Join-Path $lib "junit-platform-console-standalone-1.14.0.jar"
if (-not (Test-Path $consoleJar)) {
  Write-Host "Downloading junit-platform-console-standalone..."
  Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.14.0/junit-platform-console-standalone-1.14.0.jar" -OutFile $consoleJar
}

Write-Host "Running tests..."
& java -jar $consoleJar -cp $out --scan-classpath
exit $LASTEXITCODE

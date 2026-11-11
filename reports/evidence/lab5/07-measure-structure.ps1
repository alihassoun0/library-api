$ErrorActionPreference = 'Stop'
$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot '../../..')).Path

function Get-MethodSpan {
    param([string[]]$Lines, [string]$Signature)
    $methodStart = $null
    for ($lineIndex = 0; $lineIndex -lt $Lines.Count; $lineIndex++) {
        if ($Lines[$lineIndex] -match $Signature) {
            $methodStart = $lineIndex
            break
        }
    }
    if ($null -eq $methodStart) { throw "Method not found: $Signature" }
    $braceDepth = 0
    for ($lineIndex = $methodStart; $lineIndex -lt $Lines.Count; $lineIndex++) {
        $braceDepth += ([regex]::Matches($Lines[$lineIndex], '\{')).Count
        $braceDepth -= ([regex]::Matches($Lines[$lineIndex], '\}')).Count
        if ($braceDepth -eq 0) {
            return [ordered]@{
                StartLine = $methodStart + 1
                EndLine = $lineIndex + 1
                PhysicalLines = $lineIndex - $methodStart + 1
                NonBlankLines = @($Lines[$methodStart..$lineIndex] | Where-Object { $_.Trim() }).Count
            }
        }
    }
    throw "Unclosed method: $Signature"
}

$beforeBorrowing = @(Get-Content -LiteralPath (Join-Path $PSScriptRoot '00-BorrowingService-before.java.txt'))
$afterBorrowing = @(Get-Content -LiteralPath (Join-Path $repoRoot 'src/main/java/com/example/libraryapi/service/BorrowingService.java'))
$beforeHandler = Get-Content -Raw -LiteralPath (Join-Path $PSScriptRoot '04-extract-class-before.java.txt')
$afterHandler = Get-Content -Raw -LiteralPath (Join-Path $repoRoot 'src/main/java/com/example/libraryapi/exception/GlobalExceptionHandler.java')
$factory = Get-Content -Raw -LiteralPath (Join-Path $repoRoot 'src/main/java/com/example/libraryapi/exception/ErrorResponseFactory.java')
$beforeListing = @(Get-Content -LiteralPath (Join-Path $PSScriptRoot '02-source-before.txt'))
$afterSources = @(Get-ChildItem -LiteralPath (Join-Path $repoRoot 'src/main/java') -Recurse -Filter '*.java')
$afterPhysicalLines = 0
foreach ($sourceFile in $afterSources) {
    $afterPhysicalLines += @(Get-Content -LiteralPath $sourceFile.FullName).Count
}
$beforeBorrowingText = $beforeBorrowing -join "`n"
$afterBorrowingText = $afterBorrowing -join "`n"
$metrics = [ordered]@{
    Scope = 'Structural counts only, not cyclomatic complexity, coverage, saved effort, or a debt score.'
    CountingConvention = 'Method physical span includes declaration, closing brace, and blanks. Source lines include all physical lines; original source is the frozen 29-file listing.'
    ReturnBookBefore = Get-MethodSpan $beforeBorrowing 'public Borrowing returnBook\('
    ReturnBookAfter = Get-MethodSpan $afterBorrowing 'public Borrowing returnBook\('
    ApplyReturnStateAfter = Get-MethodSpan $afterBorrowing 'private void applyReturnState\('
    PublicFineBefore = Get-MethodSpan $beforeBorrowing 'public BigDecimal calculateFine\('
    PublicFineAfter = Get-MethodSpan $afterBorrowing 'public BigDecimal calculateFine\('
    PrivateFineAfter = Get-MethodSpan $afterBorrowing 'private BigDecimal calculateFine\('
    PolicyLiteralUseSitesBefore = ([regex]::Matches($beforeBorrowingText, 'plusDays\(14\)|return new BigDecimal\("0\.50"\)')).Count
    PolicyLiteralUseSitesAfter = ([regex]::Matches($afterBorrowingText, 'plusDays\(14\)|return new BigDecimal\("0\.50"\)')).Count
    NamedPolicyDefinitionsAfter = ([regex]::Matches($afterBorrowingText, 'private static final (?:BigDecimal DAILY_FINE_RATE|int DEFAULT_LOAN_DAYS)')).Count
    ErrorEnvelopeConstructionSitesBefore = ([regex]::Matches($beforeHandler, 'new ErrorResponseDTO\(')).Count
    ErrorEnvelopeConstructionSitesAfter = ([regex]::Matches($afterHandler + $factory, 'new ErrorResponseDTO\(')).Count
    ExceptionHandlerMappingsBefore = ([regex]::Matches($beforeHandler, '@ExceptionHandler\(')).Count
    ExceptionHandlerMappingsAfter = ([regex]::Matches($afterHandler, '@ExceptionHandler\(')).Count
    JavaSourceFilesBefore = @($beforeListing | Where-Object { $_ -match '^FILE: ' }).Count
    JavaSourceFilesAfter = $afterSources.Count
    PhysicalSourceLinesBefore = @($beforeListing | Where-Object { $_ -match '^\s*\d+: ' }).Count
    PhysicalSourceLinesAfter = $afterPhysicalLines
}
$metrics | ConvertTo-Json -Depth 5 | Set-Content -Encoding UTF8 -LiteralPath (Join-Path $PSScriptRoot '07-structural-measures.json')
$metrics | ConvertTo-Json -Depth 5

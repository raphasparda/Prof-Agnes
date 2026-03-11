$files = Get-ChildItem -Path d:\chatbot\src -Recurse -Filter *.java
foreach ($file in $files) {
    if ($file.FullName -match "target\\") { continue }
    $bytes = [System.IO.File]::ReadAllBytes($file.FullName)
    if ($bytes.Length -ge 3 -and $bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF) {
        $bytes = $bytes[3..($bytes.Length - 1)]
        [System.IO.File]::WriteAllBytes($file.FullName, $bytes)
    }
}

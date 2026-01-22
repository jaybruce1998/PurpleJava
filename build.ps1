javac -d out (Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName })
jar cfe Purple.jar purple.Main -C out . -C src/main/resources .
java -jar Purple.jar
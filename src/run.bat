@echo off
xcopy src\main\resources\FXML target\classes\FXML /E /I /Y
cd C:\Users\HP\.jdks\openjdk-26\bin
java -cp "C:\Users\HP\Documents\IntelliJIDEA\Akira\target\classes" --module-path "C:\Users\HP\.m2\repository\org\openjfx" view.Akira
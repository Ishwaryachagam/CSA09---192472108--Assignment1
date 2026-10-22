# CSA09 - Campus Smart-Parking System PowerShell Runner
$ErrorActionPreference = "Stop"

Write-Host "=====================================================================" -ForegroundColor Cyan
Write-Host "   CSA09 - CAMPUS SMART-PARKING SYSTEM : AUTOMATED TASK RUNNER" -ForegroundColor Cyan
Write-Host "=====================================================================" -ForegroundColor Cyan

# Locate javac and java
$cmdJavac = Get-Command javac -ErrorAction SilentlyContinue
if ($cmdJavac) {
    $javac = $cmdJavac.Source
} else {
    $javac = "C:\Users\rsree\.jdk17\jdk-17.0.20.1+1\bin\javac.exe"
}

$cmdJava = Get-Command java -ErrorAction SilentlyContinue
if ($cmdJava) {
    $java = $cmdJava.Source
} else {
    $java = "C:\Users\rsree\.jdk17\jdk-17.0.20.1+1\bin\java.exe"
}

Write-Host "Using Java Compiler : $javac" -ForegroundColor Green
Write-Host "Using Java Runtime  : $java" -ForegroundColor Green
Write-Host ""

$tasks = @(
    @{ Folder = "TASK1";  Main = "VehicleDemo";            Title = "Task 1: Problem Analysis & Objects" },
    @{ Folder = "TASK2";  Main = "DataRepresentationDemo"; Title = "Task 2: Data Representation" },
    @{ Folder = "TASK3";  Main = "ParkingFacility";        Title = "Task 3: Control Logic & Pseudocode" },
    @{ Folder = "TASK4";  Main = "ConstructorDemo";        Title = "Task 4: Classes, Objects & Constructors" },
    @{ Folder = "TASK5";  Main = "StaticLifecycleDemo";    Title = "Task 5: static Members & Object Life Cycle" },
    @{ Folder = "TASK6";  Main = "InheritanceDemo";        Title = "Task 6: Inheritance Hierarchy" },
    @{ Folder = "TASK7";  Main = "MethodOverridingDemo";   Title = "Task 7: Method Overriding" },
    @{ Folder = "TASK8";  Main = "PolymorphismDemo";       Title = "Task 8: Polymorphism" },
    @{ Folder = "TASK9";  Main = "AbstractionDemo";        Title = "Task 9: Abstraction" },
    @{ Folder = "TASK10"; Main = "EncapsulationDemo";      Title = "Task 10: Packages & Encapsulation" },
    @{ Folder = "TASK11"; Main = "SmartParkingSystem";     Title = "Task 11: Implementation, Testing & Defence" },
    @{ Folder = "TASK12"; Main = "DesignChallengeDemo";    Title = "Task 12: Day-4 Design Challenge" }
)

foreach ($t in $tasks) {
    Write-Host "=====================================================================" -ForegroundColor Yellow
    Write-Host "  $($t.Title) [$($t.Folder)]" -ForegroundColor Yellow
    Write-Host "=====================================================================" -ForegroundColor Yellow
    
    $source = "$($t.Folder)\$($t.Main).java"
    & $javac $source
    & $java -cp $t.Folder $t.Main
    Write-Host ""
}

Write-Host "=====================================================================" -ForegroundColor Green
Write-Host "  ALL 12 TASKS COMPILED AND EXECUTED SUCCESSFULLY!" -ForegroundColor Green
Write-Host "=====================================================================" -ForegroundColor Green

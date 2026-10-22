@echo off
setlocal enabledelayedexpansion

echo =====================================================================
echo    CSA09 - CAMPUS SMART-PARKING SYSTEM : AUTOMATED TASK RUNNER
echo =====================================================================

REM Locate Java and Javac
where javac >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    set JAVAC_CMD=javac
    set JAVA_CMD=java
) else (
    set JAVAC_CMD="C:\Users\rsree\.jdk17\jdk-17.0.20.1+1\bin\javac.exe"
    set JAVA_CMD="C:\Users\rsree\.jdk17\jdk-17.0.20.1+1\bin\java.exe"
)

echo Using Java Compiler: %JAVAC_CMD%
echo Using Java Runtime : %JAVA_CMD%
echo.

set TASKS=TASK1 TASK2 TASK3 TASK4 TASK5 TASK6 TASK7 TASK8 TASK9 TASK10 TASK11 TASK12
set MAINS=VehicleDemo DataRepresentationDemo ParkingFacility ConstructorDemo StaticLifecycleDemo InheritanceDemo MethodOverridingDemo PolymorphismDemo AbstractionDemo EncapsulationDemo SmartParkingSystem DesignChallengeDemo

set i=0
for %%T in (%TASKS%) do (
    set /a i+=1
    set j=0
    for %%M in (%MAINS%) do (
        set /a j+=1
        if !i! EQU !j! (
            echo =====================================================================
            echo  COMPILING AND RUNNING %%T (%%M)
            echo =====================================================================
            %JAVAC_CMD% %%T\%%M.java
            if !ERRORLEVEL! EQU 0 (
                %JAVA_CMD% -cp %%T %%M
            ) else (
                echo [ERROR] Failed to compile %%T\%%M.java
            )
            echo.
        )
    )
)

echo =====================================================================
echo  ALL TASKS EXECUTED SUCCESSFULLY!
echo =====================================================================
pause

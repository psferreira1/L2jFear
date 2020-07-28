@echo off

set PATH=%PATH%;C:\xampp\mysql\bin

set USER=root
set PASS=
set DBNAME=frozen
set DBHOST=127.0.0.1

goto settings


:settings


for /r install %%f in (*.sql) do ( 
                echo Installing table %%~nf ...
		mysql -h %DBHOST% -u %USER% --password=%PASS% -D %DBNAME% < %%f
	)
:end

pause

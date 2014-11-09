;set JAVA_HOME=.\jre1.8.0_25
;set CLASSPATH=.;%JAVA_HOME%\lib;%JAVA_HOME%\lib\tools.jar;%CATALINA_HOME%\lib;
;set PATH=%JAVA_HOME%\bin;$PATH
java -jar -Xms512m cnchina.jar testfolder 2>nul
pause
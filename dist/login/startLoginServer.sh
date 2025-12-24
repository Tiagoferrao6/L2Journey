#!/bin/bash

echo "L2Journey - LoginServer"
echo "------------------------------------------------------------------------------"

while true; do
    echo "Iniciando Loginserver."
    echo "------------------------------------------------------------------------------"

    java -server \
        -Dfile.encoding=UTF-8 \
        -Dorg.slf4j.simpleLogger.log.com.zaxxer.hikari=warn \
        -XX:+UseZGC \
        -Xms128m \
        -Xmx256m \
        -Dlogback.configurationFile=./configuration/logback.xml \
        -cp "./../libs/*:Loginserver.jar" \
        com.l2journey.loginserver.LoginServer

    EXIT_CODE=$?

    if [ $EXIT_CODE -eq 2 ]; then
        echo ""
        echo "Admin Restarted Login Server."
        echo ""
        continue
    elif [ $EXIT_CODE -eq 1 ]; then
        echo ""
        echo "Login Server parou inesperadamente!"
        echo ""
        break
    else
        echo ""
        echo "Login Server Terminou."
        echo ""
        break
    fi
done

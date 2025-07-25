#!/bin/bash


service mysql start

mysql -u root -e "ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root'; FLUSH PRIVILEGES;"
mysql -u root -proot -e "CREATE DATABASE IF NOT EXISTS testdb;"

service redis-server start

exec java -jar /app.jar

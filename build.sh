#!/bin/sh

git pull
./gradlew clean build
./start.sh

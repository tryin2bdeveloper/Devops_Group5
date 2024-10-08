#!/bin/bash

open -a docker

docker-compose up
docker-compose down

echo "Docker Image deleting process."
docker rmi $(docker images -q) --force
sleep 2
echo "Successful" 

echo "Docker Volume Releasing process."
docker volume rm $(docker volume ls -q) --force
sleep 2
echo "Successful"

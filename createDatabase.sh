#!/bin/bash
echo "Listing all current running docker container"
docker ps
echo
echo
echo
echo "Trying to create MongoDB for MoviT"
docker run --name=mongoMoviT -d --rm -p 27017:27017 mongo
echo "Database created"

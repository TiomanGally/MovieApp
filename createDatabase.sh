#!/bin/bash
echo "Creating database for MoviT"
echo "Listing all running docker container"
docker ps
echo "Trying to create MongoDB for MoviT"
docker run -d --name mongoDbForMoviT -p 27017:27107 mongo
echo "Database created"

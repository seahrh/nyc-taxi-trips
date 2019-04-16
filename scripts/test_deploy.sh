#!/usr/bin/env bash

printf "\n\naverage_speed_24hrs\n======================\n"

curl -X GET "http://localhost:8080/average_speed_24hrs?date=2015-04-02" -i; echo

printf "\n\ntotal_trips\n======================\n"

curl -X GET "http://localhost:8080/total_trips?start=2015-03-28&end=2015-04-01" -i; echo

printf "\n\naverage_fare_heatmap\n======================\n"

curl -X GET http://localhost:8080/average_fare_heatmap?date=2014-01-03 -i; echo

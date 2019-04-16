# nyc-taxi-trips
REST API for the [New York City Taxi Trips](https://console.cloud.google.com/marketplace/details/city-of-new-york/nyc-tlc-trips) public dataset, implemented in Scala and Play Framework 2.7.
## Requirements
- Java 8
- sbt
- Docker
## Endpoints
### Trip count
Request
```
$ curl -X GET "http://localhost:8080/total_trips?start=2015-03-28&end=2015-04-01" -i; echo
```
Response
```
HTTP/1.1 200 OK
Cache-Control: max-age: 100
Referrer-Policy: origin-when-cross-origin, strict-origin-when-cross-origin
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block
X-Content-Type-Options: nosniff
X-Permitted-Cross-Domain-Policies: master-only
Date: Tue, 16 Apr 2019 02:57:14 GMT
Content-Type: application/json
Content-Length: 225

{"data":[{"date":"2015-03-28","total_trips":586577},{"date":"2015-03-29","total_trips":486251},{"date":"2015-03-30","total_trips":436074},{"date":"2015-03-31","total_trips":481049},{"da
te":"2015-04-01","total_trips":492189}]}
```
Implementation in [tripcount](app/v1/trip/tripcount) package.
### Average fare heatmap
Request
```
$ curl -X GET http://localhost:8080/average_fare_heatmap?date=2014-01-03 -i; echo
```
Response
```
HTTP/1.1 200 OK
Cache-Control: max-age: 100
Referrer-Policy: origin-when-cross-origin, strict-origin-when-cross-origin
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block
X-Content-Type-Options: nosniff
X-Permitted-Cross-Domain-Policies: master-only
Date: Tue, 16 Apr 2019 02:57:19 GMT
Content-Type: application/json
Content-Length: 95872

{"data":[{"s2id":"89c25fc65","fare":12.49},{"s2id":"89c25fb51","fare":8},{"s2id":"89c2f63dd","fare":19.15},{"s2id":"89c25a4ef","fare":7.5},{"s2id":"89c25a51b","fare":14.61},{"s2id":"89c
2f5dd7","fare":18},{"s2id":"89c25b91b","fare":4},{"s2id":"89c2f6169","fare":20.26},{"s2id":"89c2f6049","fare":5},{"s2id":"89c2f370b","fare":12},{"s2id":"89c244c15","fare":7},{"s2id":"89
c2f35cb","fare":10.5},{"s2id":"89c25c1d7","fare":8},{"s2id":"89c2f683b","fare":15.25},{"s2id":"89c25e2c1","fare":7},{"s2id":"89c2f5cbd","fare":22},{"s2id":"89c2f432b","fare":15.86},{"s2
id":"89c2f41eb","fare":7.5},{"s2id":"89c25960f","fare":6.5}]}
```
Implementation in [averagefareheatmap](app/v1/trip/averagefareheatmap) package.
### Average speed
Request
```
$ curl -X GET "http://localhost:8080/average_speed_24hrs?date=2015-04-02" -i; echo
```
Response
```
HTTP/1.1 200 OK
Cache-Control: max-age: 100
Referrer-Policy: origin-when-cross-origin, strict-origin-when-cross-origin
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block
X-Content-Type-Options: nosniff
X-Permitted-Cross-Domain-Policies: master-only
Date: Tue, 16 Apr 2019 02:57:11 GMT
Content-Type: application/json
Content-Length: 33

{"data":[{"average_speed":14.3}]}
```
Implementation in [averagespeed](app/v1/trip/averagespeed) package.
### HTTP status codes
The API may serve the following http status codes:
- 200 OK
- 400 Bad Request
- 404 Not Found
- 500 Internal error

Same as per REST API providers like [Twitter](https://developer.twitter.com/en/docs/ads/general/guides/response-codes) and [Twilio](https://www.twilio.com/docs/verify/return-and-error-codes). 
## Deploy
Publish a local Docker image with sbt and run the Play app
```
nyc-taxi-trips$ scripts/deploy.sh
```
Note: Internet connectivity is required for the Play app to run because the data store is BigQuery.

Sanity check to see api calls are working correctly
```
nyc-taxi-trips$ scripts/test_deploy.sh | less
```
### Security best practice
I needed a self-contained way to share credentials so I decided to put the `APPLICATION_SECRET` and `GOOGLE_APPLICATION_CREDENTIALS` in the repo. This is _not_ what I would do normally, as it is very bad from a security viewpoint.

Best practice is not to keep any secrets in source control but to set them as environment variables on the target server.
## Configuration
Routing is set in [conf/routes](conf/routes).

Application config is set in [conf/application.conf](conf/application.conf). For easy tracking, I use the Java package naming convention to specify which class is using the value. This also avoids naming conflicts.
## Data store
The data store is BigQuery (run at my own expense). Reasons:
1. The source data is in BigQuery
1. API requirements are read-only.

As much as possible, the results required by the API should be pre-computed in the ETL stage.

To load the tables that will be queried by the API, ETL is performed with the scripts in [bigquery](bigquery) directory.

### Clustering
Clustering is performed on the `average_fare_by_pickup_location` table to speed up queries that have equality filter on `date` column.

Clustering is recommended for columns that have high cardinality e.g. dates. Data points that share the same date will be hashed to the same bucket. 

See [bigquery/average_fare_by_pickup_location.sql](bigquery/average_fare_by_pickup_location.sql) 

### Repositories
Repositories in the Play Framework provide an interface for the app to interact with the data store. The BigQuery implementation is found in the [dal](app/dal) package (Data Access Layer).
## Helpers
### Latitude-longitude coordinates to S2 cell
Helper methods are defined in [geom](app/geom) package.
### Input validation
Helper methods are defined in [validation](app/validation) package.


# nyc-taxi-trips
REST API for the [New York City Taxi Trips](https://console.cloud.google.com/marketplace/details/city-of-new-york/nyc-tlc-trips) public dataset, implemented in Scala and Play Framework 2.7.
## Requirements
- Java 8
- sbt
- Docker
## Endpoints

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

## Helpers
### Latitude-longitude coordinates to S2 cell
Helper methods are defined in [app/geom](app/geom) package.
### Input validation
Helper methods are defined in [app/validation](app/validation) package.


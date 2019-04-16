# nyc-taxi-trips
## Deploy
Publish a local Docker image with sbt and run the Play app
```
nyc-taxi-trips$ scripts/deploy.sh
```
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


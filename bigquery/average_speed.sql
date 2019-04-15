#standardSQL
SELECT
  d `date`,
  AVG(s) `speed`
FROM (
  SELECT
    FORMAT_DATETIME("%F",
      DATETIME_TRUNC(dropoff_datetime,
        DAY)) d,
    AVG(trip_distance / DATETIME_DIFF(dropoff_datetime,
        pickup_datetime,
        SECOND))*3600 s
  FROM
    `bigquery-public-data.new_york_taxi_trips.tlc_green_trips_2014`
  WHERE
    trip_distance > 0
    AND dropoff_datetime IS NOT NULL
    AND dropoff_datetime > pickup_datetime
  GROUP BY
    1
  UNION ALL
  SELECT
    FORMAT_DATETIME("%F",
      DATETIME_TRUNC(dropoff_datetime,
        DAY)) d,
    AVG(trip_distance / DATETIME_DIFF(dropoff_datetime,
        pickup_datetime,
        SECOND))*3600 s
  FROM
    `bigquery-public-data.new_york_taxi_trips.tlc_green_trips_2015`
  WHERE
    trip_distance > 0
    AND dropoff_datetime IS NOT NULL
    AND dropoff_datetime > pickup_datetime
  GROUP BY
    1
  UNION ALL
  SELECT
    FORMAT_DATETIME("%F",
      DATETIME_TRUNC(dropoff_datetime,
        DAY)) d,
    AVG(trip_distance / DATETIME_DIFF(dropoff_datetime,
        pickup_datetime,
        SECOND))*3600 s
  FROM
    `bigquery-public-data.new_york_taxi_trips.tlc_green_trips_2016`
  WHERE
    trip_distance > 0
    AND dropoff_datetime IS NOT NULL
    AND dropoff_datetime > pickup_datetime
  GROUP BY
    1
  UNION ALL
  SELECT
    FORMAT_DATETIME("%F",
      DATETIME_TRUNC(dropoff_datetime,
        DAY)) d,
    AVG(trip_distance / DATETIME_DIFF(dropoff_datetime,
        pickup_datetime,
        SECOND))*3600 s
  FROM
    `bigquery-public-data.new_york_taxi_trips.tlc_green_trips_2017`
  WHERE
    trip_distance > 0
    AND dropoff_datetime IS NOT NULL
    AND dropoff_datetime > pickup_datetime
  GROUP BY
    1
  UNION ALL
  SELECT
    FORMAT_DATETIME("%F",
      DATETIME_TRUNC(dropoff_datetime,
        DAY)) d,
    AVG(trip_distance / DATETIME_DIFF(dropoff_datetime,
        pickup_datetime,
        SECOND))*3600 s
  FROM
    `bigquery-public-data.new_york_taxi_trips.tlc_yellow_trips_2015`
  WHERE
    trip_distance > 0
    AND dropoff_datetime IS NOT NULL
    AND dropoff_datetime > pickup_datetime
  GROUP BY
    1
  UNION ALL
  SELECT
    FORMAT_DATETIME("%F",
      DATETIME_TRUNC(dropoff_datetime,
        DAY)) d,
    AVG(trip_distance / DATETIME_DIFF(dropoff_datetime,
        pickup_datetime,
        SECOND))*3600 s
  FROM
    `bigquery-public-data.new_york_taxi_trips.tlc_yellow_trips_2016`
  WHERE
    trip_distance > 0
    AND dropoff_datetime IS NOT NULL
    AND dropoff_datetime > pickup_datetime
  GROUP BY
    1
  UNION ALL
  SELECT
    FORMAT_DATETIME("%F",
      DATETIME_TRUNC(dropoff_datetime,
        DAY)) d,
    AVG(trip_distance / DATETIME_DIFF(dropoff_datetime,
        pickup_datetime,
        SECOND))*3600 s
  FROM
    `bigquery-public-data.new_york_taxi_trips.tlc_yellow_trips_2017`
  WHERE
    trip_distance > 0
    AND dropoff_datetime IS NOT NULL
    AND dropoff_datetime > pickup_datetime
  GROUP BY
    1 ) t1
GROUP BY
  1
ORDER BY
  1;
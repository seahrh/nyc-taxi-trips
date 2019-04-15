#standardSQL
SELECT
  d `date`,
  lat,
  lng,
  f `fare`,
  DATE(2019, 4, 1) `_date`
FROM (
  SELECT
    FORMAT_DATETIME("%F",
      DATETIME_TRUNC(pickup_datetime,
        DAY)) d,
    pickup_latitude lat,
    pickup_longitude lng,
    AVG(fare_amount) f
  FROM
    `bigquery-public-data.new_york_taxi_trips.tlc_green_trips_2014`
  WHERE
    pickup_datetime IS NOT NULL
    AND fare_amount IS NOT NULL
    AND pickup_latitude IS NOT NULL
    AND pickup_longitude IS NOT NULL
  GROUP BY
    1,
    2,
    3
  UNION ALL
  SELECT
    FORMAT_DATETIME("%F",
      DATETIME_TRUNC(pickup_datetime,
        DAY)) d,
    pickup_latitude lat,
    pickup_longitude lng,
    AVG(fare_amount) f
  FROM
    `bigquery-public-data.new_york_taxi_trips.tlc_green_trips_2015`
  WHERE
    pickup_datetime IS NOT NULL
    AND fare_amount IS NOT NULL
    AND pickup_latitude IS NOT NULL
    AND pickup_longitude IS NOT NULL
  GROUP BY
    1,
    2,
    3
  UNION ALL
  SELECT
    FORMAT_DATETIME("%F",
      DATETIME_TRUNC(pickup_datetime,
        DAY)) d,
    pickup_latitude lat,
    pickup_longitude lng,
    AVG(fare_amount) f
  FROM
    `bigquery-public-data.new_york_taxi_trips.tlc_green_trips_2016`
  WHERE
    pickup_datetime IS NOT NULL
    AND fare_amount IS NOT NULL
    AND pickup_latitude IS NOT NULL
    AND pickup_longitude IS NOT NULL
  GROUP BY
    1,
    2,
    3
  UNION ALL
  SELECT
    FORMAT_DATETIME("%F",
      DATETIME_TRUNC(pickup_datetime,
        DAY)) d,
    pickup_latitude lat,
    pickup_longitude lng,
    AVG(fare_amount) f
  FROM
    `bigquery-public-data.new_york_taxi_trips.tlc_green_trips_2017`
  WHERE
    pickup_datetime IS NOT NULL
    AND fare_amount IS NOT NULL
    AND pickup_latitude IS NOT NULL
    AND pickup_longitude IS NOT NULL
  GROUP BY
    1,
    2,
    3
  UNION ALL
  SELECT
    FORMAT_DATETIME("%F",
      DATETIME_TRUNC(pickup_datetime,
        DAY)) d,
    pickup_latitude lat,
    pickup_longitude lng,
    AVG(fare_amount) f
  FROM
    `bigquery-public-data.new_york_taxi_trips.tlc_yellow_trips_2015`
  WHERE
    pickup_datetime IS NOT NULL
    AND fare_amount IS NOT NULL
    AND pickup_latitude IS NOT NULL
    AND pickup_longitude IS NOT NULL
  GROUP BY
    1,
    2,
    3
  UNION ALL
  SELECT
    FORMAT_DATETIME("%F",
      DATETIME_TRUNC(pickup_datetime,
        DAY)) d,
    pickup_latitude lat,
    pickup_longitude lng,
    AVG(fare_amount) f
  FROM
    `bigquery-public-data.new_york_taxi_trips.tlc_yellow_trips_2016`
  WHERE
    pickup_datetime IS NOT NULL
    AND fare_amount IS NOT NULL
    AND pickup_latitude IS NOT NULL
    AND pickup_longitude IS NOT NULL
  GROUP BY
    1,
    2,
    3
  UNION ALL
  SELECT
    FORMAT_DATETIME("%F",
      DATETIME_TRUNC(pickup_datetime,
        DAY)) d,
    pickup_latitude lat,
    pickup_longitude lng,
    AVG(fare_amount) f
  FROM
    `bigquery-public-data.new_york_taxi_trips.tlc_yellow_trips_2017`
  WHERE
    pickup_datetime IS NOT NULL
    AND fare_amount IS NOT NULL
    AND pickup_latitude IS NOT NULL
    AND pickup_longitude IS NOT NULL
  GROUP BY
    1,
    2,
    3 ) t1
SELECT
  d `date`,
  n `count`
FROM (
  SELECT
    FORMAT_DATETIME("%F",
      DATETIME_TRUNC(pickup_datetime,
        DAY)) d,
    COUNT(1) n
  FROM
    `bigquery-public-data.new_york_taxi_trips.tlc_green_trips_2014`
  WHERE
    pickup_datetime IS NOT NULL
  GROUP BY
    1
  UNION ALL
  SELECT
    FORMAT_DATETIME("%F",
      DATETIME_TRUNC(pickup_datetime,
        DAY)) d,
    COUNT(1) n
  FROM
    `bigquery-public-data.new_york_taxi_trips.tlc_green_trips_2015`
  WHERE
    pickup_datetime IS NOT NULL
  GROUP BY
    1
  UNION ALL
  SELECT
    FORMAT_DATETIME("%F",
      DATETIME_TRUNC(pickup_datetime,
        DAY)) d,
    COUNT(1) n
  FROM
    `bigquery-public-data.new_york_taxi_trips.tlc_green_trips_2016`
  WHERE
    pickup_datetime IS NOT NULL
  GROUP BY
    1
  UNION ALL
  SELECT
    FORMAT_DATETIME("%F",
      DATETIME_TRUNC(pickup_datetime,
        DAY)) d,
    COUNT(1) n
  FROM
    `bigquery-public-data.new_york_taxi_trips.tlc_green_trips_2017`
  WHERE
    pickup_datetime IS NOT NULL
  GROUP BY
    1
  UNION ALL
  SELECT
    FORMAT_DATETIME("%F",
      DATETIME_TRUNC(pickup_datetime,
        DAY)) d,
    COUNT(1) n
  FROM
    `bigquery-public-data.new_york_taxi_trips.tlc_yellow_trips_2015`
  WHERE
    pickup_datetime IS NOT NULL
  GROUP BY
    1
  UNION ALL
  SELECT
    FORMAT_DATETIME("%F",
      DATETIME_TRUNC(pickup_datetime,
        DAY)) d,
    COUNT(1) n
  FROM
    `bigquery-public-data.new_york_taxi_trips.tlc_yellow_trips_2016`
  WHERE
    pickup_datetime IS NOT NULL
  GROUP BY
    1
  UNION ALL
  SELECT
    FORMAT_DATETIME("%F",
      DATETIME_TRUNC(pickup_datetime,
        DAY)) d,
    COUNT(1) n
  FROM
    `bigquery-public-data.new_york_taxi_trips.tlc_yellow_trips_2017`
  WHERE
    pickup_datetime IS NOT NULL
  GROUP BY
    1 ) t1
ORDER BY
  1;
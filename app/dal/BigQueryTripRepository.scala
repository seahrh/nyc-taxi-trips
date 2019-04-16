package dal

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.google.cloud.bigquery.{BigQuery, BigQueryOptions, FieldValueList, QueryJobConfiguration, TableResult}
import javax.inject.{Inject, Singleton}
import play.api.{Configuration, MarkerContext}
import util.Logging

import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future

/**
  * A BigQuery implementation for the Trip Repository.
  */
@Singleton
class BigQueryTripRepository @Inject()(conf: Configuration)
                                      (implicit ec: RepositoryExecutionContext)
  extends TripRepository with Logging {

  override val dateFormat: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

  private val bigquery: BigQuery = BigQueryOptions.getDefaultInstance.getService

  private val PROJECT: String = conf.get[String]("dal.BigQueryTripRepository.PROJECT")

  private val DATASET: String = conf.get[String]("dal.BigQueryTripRepository.DATASET")

  override def avgSpeed(date: LocalDate)
                       (implicit mc: MarkerContext): Future[Option[AverageSpeed]] = Future {
    log.trace(s"avgSpeed: date=$date")
    val ds: String = date.format(dateFormat)
    val sql: String =
      s"""
         |SELECT `date`,`speed`
         |FROM `$PROJECT.$DATASET.average_speed`
         |WHERE `date`="$ds"
       """.stripMargin
    val config: QueryJobConfiguration = QueryJobConfiguration.newBuilder(sql)
      .setUseLegacySql(false)
      .build
    bigquery.query(config).iterateAll().asScala.headOption match {
      case Some(row) =>
        Option(AverageSpeed(
          date = row.get("date").getStringValue,
          speed = row.get("speed").getDoubleValue.toFloat
        ))
      case _ => None
    }
  }

  override def avgFareByPickupLocation(date: LocalDate)
                                      (implicit mc: MarkerContext):
  Future[Seq[AverageFareByPickupLocation]] = Future {
    log.trace(s"avgFareByPickupLocation: date=$date")
    val ds: String = date.format(dateFormat)
    val partitionDate: String = "2019-04-01"
    val sql: String =
      s"""
         |SELECT `lat`,`lng`,`fare`
         |FROM `$PROJECT.$DATASET.average_fare_by_pickup_location`
         |WHERE `_date`=PARSE_DATE("%F","$partitionDate")
         |AND `date`="$ds"
         |AND `fare`>=1
       """.stripMargin
    val config: QueryJobConfiguration = QueryJobConfiguration.newBuilder(sql)
      .setUseLegacySql(false)
      .build
    val result: TableResult = bigquery.query(config)
    val arr: ArrayBuffer[AverageFareByPickupLocation] = ArrayBuffer()
    for (row: FieldValueList <- result.iterateAll().asScala) {
      arr += AverageFareByPickupLocation(
        lat = row.get("lat").getDoubleValue.toFloat,
        lng = row.get("lng").getDoubleValue.toFloat,
        fare = row.get("fare").getDoubleValue.toFloat
      )
    }
    arr
  }

  override def tripCount(from: LocalDate, to: LocalDate)
                        (implicit mc: MarkerContext): Future[Seq[TripCount]] = Future {
    log.trace(s"tripCount: from=$from, to=$to")
    val froms: String = from.format(dateFormat)
    val tos: String = to.format(dateFormat)
    val sql: String =
      s"""
         |SELECT `date`,`count`
         |FROM `$PROJECT.$DATASET.trip_count`
         |WHERE `date` between "$froms" and "$tos"
       """.stripMargin
    val config: QueryJobConfiguration = QueryJobConfiguration.newBuilder(sql)
      .setUseLegacySql(false)
      .build
    val result: TableResult = bigquery.query(config)
    val arr: ArrayBuffer[TripCount] = ArrayBuffer()
    for (row: FieldValueList <- result.iterateAll().asScala) {
      arr += TripCount(
        date = row.get("date").getStringValue,
        count = row.get("count").getLongValue
      )
    }
    arr
  }
}

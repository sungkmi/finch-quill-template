package db

import java.sql.Connection
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import com.opentable.db.postgres.embedded.EmbeddedPostgres
import com.twitter.finagle.Postgres
import com.twitter.finagle.postgres.PostgresClient
import liquibase.{Contexts, Liquibase}
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.FileSystemResourceAccessor

object TestPostgres {

  val location: String = "db/src/main/migrations/changelog.xml"

  implicit val ec = ExecutionContext.global

  val pgFuture: Future[EmbeddedPostgres] = Future {
    val pg = EmbeddedPostgres.start
    println(s"===> Setup postgres: ${pg.getPort}")

    var liquibase: Liquibase = null
    try {
      val connection = pg.getPostgresDatabase.getConnection()
      println(s"===> With connection: $connection")
      val database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection))
      liquibase = new Liquibase(location, new FileSystemResourceAccessor(), database)
      println("===> Updating Liquibase")
      liquibase.update(new Contexts())
    } catch {
      case e: Throwable => e.printStackTrace
    } finally {
      liquibase.forceReleaseLocks()
    }
    println(s"===> Liquibase ready")

    pg
  }

  def setup(): Unit = {
    Await.ready(pgFuture, 10.seconds)
    ()
  }

  def teardown(): Unit = {
    Await.ready(pgFuture.map{_.close()}, 10.seconds)
    ()
  }

  lazy private val pg: EmbeddedPostgres = Await.result(pgFuture, 15.seconds)

  lazy val testClient:PostgresClient = Postgres.Client()
    .withCredentials("postgres", Some("postgres"))
    .database("postgres")
    .withSessionPool.maxSize(1)
    .withBinaryResults(true)
    .withBinaryParams(true)
    .newRichClient(s"127.0.0.1:${pg.getPort}")

  lazy val connection: Connection = pg.getPostgresDatabase.getConnection()
}

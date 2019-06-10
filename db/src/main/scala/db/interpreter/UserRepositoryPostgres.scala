package db
package interpreter

import java.time.Instant
import java.util.UUID
import com.github.ghik.silencer.silent
import com.twitter.finagle.postgres.PostgresClient
import com.twitter.finagle.postgres.codec.ServerError
import com.twitter.util.{Future => TwitterFuture}
import io.getquill.{FinaglePostgresContext, NamingStrategy, PostgresEscape, SnakeCase}
import algebra.UserRepository
import model.User

class UserRepositoryPostgres(client: PostgresClient) extends UserRepository[TwitterFuture] {

  private val ctx = new FinaglePostgresContext(NamingStrategy(SnakeCase, PostgresEscape), client)
  import ctx._
  @silent("private val") // required to the quill macro
  implicit private val instantEncoder: Encoder[Instant] = encoder[Instant]

  def selectAll(offset: Int, limit: Int): TwitterFuture[Seq[User]] = run {
    query[User].sortBy(_.email).drop(lift(offset)).take(lift(limit))
  }

  def select(id: UUID): TwitterFuture[Option[User]] = {
    run(query[User].filter(_.id == lift(id))).map(_.headOption)
  }
  def selectByEmail(email: String): TwitterFuture[Option[User]] = {
    run(query[User].filter(_.email == lift(email))).map(_.headOption)
  }
  def insert(email: String, phoneNumber: String): TwitterFuture[Either[String, UUID]] = {
    val id = UUID.randomUUID()
    run{
      query[User].insert(_.id -> lift(id), _.email -> lift(email), _.phoneNumber -> lift(phoneNumber))
    }.map(_ => Right(id)).rescue {
      case se: ServerError if se.message contains "duplicate key value violates unique constraint" =>
        TwitterFuture.value(Left(s"duplicate email: $email"))
    }
  }
  def update(email: String, phoneNumber: String): TwitterFuture[Long] = run {
    query[User].filter(_.email == lift(email)).update(_.phoneNumber -> lift(phoneNumber), _.updatedAt -> lift(Instant.now))
  }
}

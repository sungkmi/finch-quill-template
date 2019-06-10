package db
package algebra

import java.util.UUID
import model.User

trait UserRepository[F[_]] {
  def select(id: UUID): F[Option[User]]
  def selectByEmail(email: String): F[Option[User]]
  def insert(email: String, phoneNumber: String): F[Either[String, UUID]]
  def update(email: String, phoneNumber: String): F[Long]
}

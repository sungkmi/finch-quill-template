package db
package interpreter

import com.github.ghik.silencer.silent
import com.twitter.util.Await
import model.User
import utest._

object UserRepositoryPostgresTest extends DbTestSuite{

  @silent("pattern var")
  val tests = Tests{

    val sampleEmail = "some@e.mail"
    val samplePhoneNumber = "123-456-7890"

    val repo = new UserRepositoryPostgres(TestPostgres.testClient)

    def await[A](awaitable: com.twitter.util.Awaitable[A]): A = Await.result(awaitable)

    "Select empty database" - {
      await(for {
        userOption <- repo.selectByEmail(sampleEmail)
      } yield {
        assert(userOption.isEmpty)
      })
    }

    "Insert and select" - {
      await(for {
        _ <- repo.insert(sampleEmail, samplePhoneNumber)
        user <- repo.selectByEmail(sampleEmail)
      } yield {
        assertMatch(user){ case Some(User(_, sampleEmail, samplePhoneNumber, _, _)) => }
      })
    }

    "Insert and selectAll" - {
      await(for {
        _ <- repo.insert(sampleEmail, samplePhoneNumber)
        users <- repo.selectAll(offset = 0, limit = 10)
      } yield {
        assertMatch(users){case Seq(User(_, sampleEmail, samplePhoneNumber, _, _)) => }
      })
    }

    "Insert already inserted" - {
      await(for {
        _ <- repo.insert(sampleEmail, samplePhoneNumber)
        result <- repo.insert(sampleEmail, samplePhoneNumber)
      } yield {
        assertMatch(result){ case Left(_) => }
      })
    }

    "Update" - {

      val updatedPhoneNumber = "234-567-8901"

      await(for {
        _ <- repo.insert(sampleEmail, samplePhoneNumber)
        _ <- repo.update(sampleEmail, updatedPhoneNumber)
        user <- repo.selectByEmail(sampleEmail)
      } yield {
        assertMatch(user){case Some(User(_, sampleEmail, updatedPhoneNumber, _, _)) => }
      })
    }

    "Try to update unexisting item" - {

      val updatedPhoneNumber = "234-567-8901"

      await(for {
        count <- repo.update(sampleEmail, updatedPhoneNumber)
      } yield {
        assert(count == 0L)
      })
    }
  }
}

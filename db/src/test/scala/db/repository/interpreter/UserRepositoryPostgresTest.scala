package db
package interpreter

import com.twitter.util.Await

class UserRepositoryPostgresTest extends DbTestSuite {

  val sampleEmail = "some@e.mail"
  val samplePhoneNumber = "123-456-7890"

  val repo = new UserRepositoryPostgres(TestPostgres.testClient)

  def await[A](awaitable: com.twitter.util.Awaitable[A]): A = Await.result(awaitable)

  test("Select empty database") {
    await(for {
      userOption <- repo.selectByEmail(sampleEmail)
    } yield {
      assert(userOption.isEmpty)
    })
  }

  test("Insert and select") {
    await(for {
      _ <- repo.insert(sampleEmail, samplePhoneNumber)
      user <- repo.selectByEmail(sampleEmail)
    } yield {
      user match {
        case Some(user) =>
          assert(user.email == sampleEmail && user.phoneNumber == samplePhoneNumber)
        case _ =>
          fail("No user is selected")
      }
    })
  }

  test("Insert and selectAll") {
    await(for {
      _ <- repo.insert(sampleEmail, samplePhoneNumber)
      users <- repo.selectAll(offset = 0, limit = 10)
    } yield {
      users match {
        case Seq(user) =>
          assert(user.email == sampleEmail && user.phoneNumber == samplePhoneNumber)
        case other =>
          fail(s"Single user expects to be selected, but: $other")
      }
    })
  }

  test("Insert already inserted") {
    await(for {
      _ <- repo.insert(sampleEmail, samplePhoneNumber)
      result <- repo.insert(sampleEmail, samplePhoneNumber)
    } yield {
      assert(result.isLeft)
    })
  }

  test("Update") {

    val updatedPhoneNumber = "234-567-8901"

    await(for {
      _ <- repo.insert(sampleEmail, samplePhoneNumber)
      _ <- repo.update(sampleEmail, updatedPhoneNumber)
      userOption <- repo.selectByEmail(sampleEmail)
    } yield {
      userOption match {
        case Some(user) =>
          assert(user.email == sampleEmail && user.phoneNumber == updatedPhoneNumber)
        case None =>
          fail(s"user is not selected with sample email")
      }
    })
  }

  test("Try to update unexisting item") {

    val updatedPhoneNumber = "234-567-8901"

    await(for {
      count <- repo.update(sampleEmail, updatedPhoneNumber)
    } yield {
      assert(count == 0L)
    })
  }
}

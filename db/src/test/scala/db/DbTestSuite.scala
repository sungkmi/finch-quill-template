package db

trait DbTestSuite extends munit.FunSuite {

  val Tables: Seq[String] = Seq(
    "user",
  )

  override def afterEach(context: AfterEach): Unit = {
    for (table <- Tables) {
      TestPostgres.connection.setAutoCommit(false)
      val statement = TestPostgres.connection.createStatement()
      statement.executeUpdate(s"""ALTER TABLE "$table" DISABLE TRIGGER ALL;""")
      statement.executeUpdate(s"""TRUNCATE TABLE "$table";""")
      statement.executeUpdate(s"""ALTER TABLE "$table" DISABLE TRIGGER ALL;""")
      TestPostgres.connection.commit()
    }
  }

  override def beforeAll() = {
    TestPostgres.setup
    println(s"Setting up DbTestFramework: ${TestPostgres.testClient}")
  }

  override def afterAll() = {
    TestPostgres.teardown
    println(s"Tearing down DbTestFramework: ${TestPostgres.testClient}")
  }
}

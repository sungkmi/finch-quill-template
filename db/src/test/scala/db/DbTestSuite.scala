package db

trait DbTestSuite extends utest.TestSuite {

  val Tables: Seq[String] = Seq(
    "user",
  )

  override def utestAfterEach(path: Seq[String]): Unit = {
    for (table <- Tables) {
      TestPostgres.connection.setAutoCommit(false)
      val statement = TestPostgres.connection.createStatement()
      statement.executeUpdate(s"""ALTER TABLE "$table" DISABLE TRIGGER ALL;""")
      statement.executeUpdate(s"""TRUNCATE TABLE "$table";""")
      statement.executeUpdate(s"""ALTER TABLE "$table" DISABLE TRIGGER ALL;""")
      TestPostgres.connection.commit()
    }
  }
}

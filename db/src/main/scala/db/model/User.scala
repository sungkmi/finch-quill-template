package db.model

import java.time.Instant
import java.util.UUID

case class User(
  id: UUID,
  email: String,
  phoneNumber: String,
  createdAt: Instant,
  updatedAt: Instant,
)

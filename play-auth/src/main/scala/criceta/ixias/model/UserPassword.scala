package criceta.play.auth.ixias.model

import ixias.model._
import ixias.security.PBKDF2
import java.time.LocalDateTime

/** User hashed password */
case class UserPassword(
  id:        Option[User.Id], // Id of user
  hash:      String,          // Hashed string of password
  updatedAt: LocalDateTime = NOW,
  createdAt: LocalDateTime = NOW
) extends EntityModel[User.Id] {

  /** Verify input string */
  def verify(input: String): Boolean =
    PBKDF2.compare(input, hash)
}

/** Companion object */
object UserPassword {

  /** Create a hashed password */
  def create(id: User.Id, raw: String): UserPassword#EmbeddedId =
    UserPassword(Some(id), hash(raw)).toEmbeddedId

  /** Hash password */
  def hash(raw: String): String = PBKDF2.hash(raw)
}

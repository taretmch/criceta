package criceta.play.auth.ixias.model

import ixias.model._
import javax.mail.internet.InternetAddress
import java.time.LocalDateTime

/** Common user */
import User._
case class User(
  id:        Option[Id],   // Id of user
  email:     EmailAddress, // Email
  updatedAt: LocalDateTime = NOW,
  createdAt: LocalDateTime = NOW
) extends EntityModel[Id]

/** Companion object */
object User {

  /** Definition of Id type */
  val  Id = the[Identity[Id]]
  type Id = Long @@ User

  /** Value class: Email address
   *
   * This class uses RFC822 syntax to represent internet mail address
   */
  case class EmailAddress(v: InternetAddress) {
    /** Get email address as String */
    val email: String = v.getAddress
  }
  object EmailAddress {
    def apply(email: String): EmailAddress =
      EmailAddress(new InternetAddress(email))
  }
}

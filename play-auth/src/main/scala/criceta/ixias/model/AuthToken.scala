package criceta.play.auth.ixias.model

import ixias.model._
import ixias.play.api.auth.token.Token.AuthenticityToken
import java.time.{ Duration, LocalDateTime }

/** Token for authentication */
import AuthToken._
case class AuthToken(
  id:        Option[Id],              // Id of auth-token
  uid:       User.Id,                 // Id of user
  token:     AuthenticityToken,       // Token string
  expiry:    Option[Duration] = None, // Expiry
  updatedAt: LocalDateTime    = NOW,
  createdAt: LocalDateTime    = NOW
) extends EntityModel[Id]

/** Companion Object */
object AuthToken {

  /** Definition of Id type */
  val  Id = the[Identity[Id]]
  type Id = Long @@ AuthToken
}

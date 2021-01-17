package criceta.play.auth.ixias.play.mvc

import criceta.play.auth.ixias.model.User
import criceta.play.auth.ixias.persistence.default.UserRepository
import criceta.play.auth.ixias.play.container.Container

import javax.inject._
import java.time.Duration
import scala.concurrent.{ Future, ExecutionContext }

import play.api.Environment
import play.api.mvc.RequestHeader

import ixias.play.api.auth.token.Token
import ixias.play.api.auth.token.TokenViaSession
import ixias.play.api.auth.container.{ Container => ixiasContainer }

/** Authentication profile */
case class AuthProfile @Inject() (
  val container: Container
)(implicit ec: ExecutionContext)
  extends ixias.play.api.auth.mvc.AuthProfile[User.Id, User, Unit] {

  val env: Environment                   = Environment.simple()
  val tokenAccessor: Token               = TokenViaSession("user")
  val datastore: ixiasContainer[Id]      = container
  val executionContext: ExecutionContext = ec

  /** Resolve authenticated resource by the identity */
  def resolve(id: Id)(implicit rh: RequestHeader): Future[Option[AuthEntity]] =
    UserRepository.get(id)

  /** Resolve timeout of session */
  def sessionTimeout(implicit request: RequestHeader): Option[Duration] =
    Some(Duration.ofHours(24L))
}

package criceta.play.auth.ixias.play.container

import criceta.play.auth.ixias.model.{ User, AuthToken }
import criceta.play.auth.ixias.persistence.default.AuthTokenRepository

import javax.inject._
import play.api.mvc.RequestHeader
import scala.concurrent.{ Future, ExecutionContext }
import java.time.Duration

import ixias.security.TokenGenerator
import ixias.util.Configuration
import ixias.play.api.auth.token.Token.AuthenticityToken

/** The container for session's tokens */
case class Container @Inject() ()(implicit ec: ExecutionContext)
  extends ixias.play.api.auth.container.Container[User.Id] {

  /** The configuration */
  private val config = Configuration()

  /** The keys of configuration */
  protected val CF_TOKEN_LENGTH = "session.token.length"

  /** Token length */
  lazy val tokenLength: Int =
    config.get[Option[Int]](CF_TOKEN_LENGTH).getOrElse(30)

  /** The execution context */
  val executionContext: ExecutionContext = ec

  /** The first callback function
   * when the session is started automatically or manually
   */
  def open(uid: Id, expiry: Option[Duration])
    (implicit request: RequestHeader): Future[AuthenticityToken] =
      AuthTokenRepository.findByUserId(uid) flatMap {
        case Some(authToken) => Future.successful(authToken.v.token)
        case None            => {
          val token     = AuthenticityToken(TokenGenerator().next(tokenLength))
          val authToken = AuthToken(None, uid, token, expiry).toWithNoId
          for {
            _ <- AuthTokenRepository.add(authToken)
          } yield token
        }
      }

  /** Set the timeout setting */
  def setTimeout(token: AuthenticityToken, expiry: Option[Duration])
    (implicit request: RequestHeader): Future[Unit] =
      for {
        authTokenOpt <- AuthTokenRepository.findByToken(token)
        _            <- authTokenOpt match {
          case Some(authToken) =>
            AuthTokenRepository.update(authToken.map(_.copy(expiry = expiry)))
          case None            => Future.successful(None)
        }
      } yield ()

  /** The read callback must always return
   * a user id or none if there is no data to read
   */
  def read(token: AuthenticityToken)
    (implicit request: RequestHeader): Future[Option[Id]] =
      for {
        authTokenOpt <- AuthTokenRepository.findByToken(token)
      } yield authTokenOpt.map(_.v.uid)

  /** This callback is executed when a session is destroyed */
  def destroy(token: AuthenticityToken)
    (implicit request: RequestHeader): Future[Unit] =
      for {
        authTokenOpt <- AuthTokenRepository.findByToken(token)
        _            <- authTokenOpt match {
          case Some(authToken) => AuthTokenRepository.remove(authToken.id)
          case None            => Future.successful(None)
        }
      } yield ()
}


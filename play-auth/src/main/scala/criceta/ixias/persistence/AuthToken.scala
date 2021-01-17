package criceta.play.auth.ixias.persistence

import scala.concurrent.Future
import slick.jdbc.JdbcProfile

import ixias.play.api.auth.token.Token.AuthenticityToken
import ixias.persistence.SlickRepository
import criceta.play.auth.ixias.model.{ AuthToken, User }

/** AuthTokenRepository: run database actions for auth-token */
case class AuthTokenRepository[P <: JdbcProfile]()(implicit val driver: P)
  extends SlickRepository[AuthToken.Id, AuthToken, P]
  with db.SlickResourceProvider[P] {

  import api._

  /** Get data */
  def get(id: Id): Future[Option[EntityEmbeddedId]] =
    RunDBAction(AuthTokenTable, "slave") { _
      .filter(_.id === id)
      .result.headOption
    }

  /** Get data by user id */
  def findByUserId(uid: User.Id): Future[Option[EntityEmbeddedId]] =
    RunDBAction(AuthTokenTable, "slave") { _
      .filter(_.uid === uid)
      .result.headOption
    }

  /** Get data by token */
  def findByToken(token: AuthenticityToken): Future[Option[EntityEmbeddedId]] =
    RunDBAction(AuthTokenTable, "slave") { _
      .filter(_.token === token)
      .result.headOption
    }

  /** Add data */
  def add(entity: EntityWithNoId): Future[Id] =
    RunDBAction(AuthTokenTable) { slick =>
      slick returning slick.map(_.id) += entity.v
    }

  /** Update data */
  def update(entity: EntityEmbeddedId): Future[Option[EntityEmbeddedId]] =
    RunDBAction(AuthTokenTable) { slick =>
      val row = slick.filter(_.id === entity.id)
      for {
        old <- row.result.headOption
        _   <- old match {
          case None    => DBIO.successful(0)
          case Some(_) => row.update(entity.v)
        }
      } yield old
    }

  /** Delete data */
  def remove(id: Id): Future[Option[EntityEmbeddedId]] =
    RunDBAction(AuthTokenTable) { slick =>
      val row = slick.filter(_.id === id)
      for {
        old <- row.result.headOption
        _   <- old match {
          case None    => DBIO.successful(0)
          case Some(_) => row.delete
        }
      } yield old
    }
}

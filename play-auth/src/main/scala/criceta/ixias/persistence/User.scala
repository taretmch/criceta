package criceta.play.auth.ixias.persistence

import scala.concurrent.Future
import slick.jdbc.JdbcProfile

import ixias.persistence.SlickRepository
import criceta.play.auth.ixias.model.User

/** UserRepository: run database actions for user */
case class UserRepository[P <: JdbcProfile]()(implicit val driver: P)
  extends SlickRepository[User.Id, User, P]
  with db.SlickResourceProvider[P] {

  import api._

  /** Get data */
  def get(id: Id): Future[Option[EntityEmbeddedId]] =
    RunDBAction(UserTable, "slave") { _
      .filter(_.id === id)
      .result.headOption
    }

  /** Get data by email */
  def findByEmail(email: String): Future[Option[EntityEmbeddedId]] =
    RunDBAction(UserTable, "slave") { _
      .filter(_.email === email)
      .result.headOption
    }

  /** Add data */
  def add(entity: EntityWithNoId): Future[Id] =
    RunDBAction(UserTable) { slick =>
      slick returning slick.map(_.id) += entity.v
    }

  /** Update data */
  def update(entity: EntityEmbeddedId): Future[Option[EntityEmbeddedId]] =
    RunDBAction(UserTable) { slick =>
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
    RunDBAction(UserTable) { slick =>
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

package criceta.play.auth.ixias.persistence

import scala.concurrent.Future
import slick.jdbc.JdbcProfile

import ixias.persistence.SlickRepository
import criceta.play.auth.ixias.model.{ User, UserPassword }

/** UserPasswordRepository: run database actions for password */
case class UserPasswordRepository[P <: JdbcProfile]()(implicit val driver: P)
  extends SlickRepository[User.Id, UserPassword, P]
  with db.SlickResourceProvider[P] {

  import api._

  /** Get data */
  def get(id: Id): Future[Option[EntityEmbeddedId]] =
    RunDBAction(UserPasswordTable, "slave") { _
      .filter(_.id === id)
      .result.headOption
    }

  /** Add data */
  def insert(entity: EntityEmbeddedId): Future[Id] =
    RunDBAction(UserPasswordTable) { slick => {
      val row = slick.filter(_.id === entity.id)
      for {
        entityOpt <- row.result.headOption
        _         <- entityOpt match {
          case None    => slick += entity.v
          case Some(_) => throw new IllegalArgumentException("UserPasswordRepository: Duplicate entity id")
        }
      } yield entity.id
    }}

  /** Update data */
  def update(entity: EntityEmbeddedId): Future[Option[EntityEmbeddedId]] =
    RunDBAction(UserPasswordTable) { slick =>
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
    RunDBAction(UserPasswordTable) { slick =>
      val row = slick.filter(_.id === id)
      for {
        old <- row.result.headOption
        _   <- old match {
          case None    => DBIO.successful(0)
          case Some(_) => row.delete
        }
      } yield old
    }

  @deprecated("use insert: EntityEmbeddedId => Future[Id]", "1.0.0")
  def add(entity: EntityWithNoId): Future[Id] =
    throw new UnsupportedOperationException("UserPasswordRepository.add: EntityWithNoId => Future[Id]")
}

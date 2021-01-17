package criceta.play.auth.ixias.persistence.db

import java.time.LocalDateTime
import slick.jdbc.JdbcProfile

import criceta.play.auth.ixias.model.{ User, UserPassword }
import ixias.persistence.model.Table

/** UserPasswordTable: Mapping between UserPassword model and MySQL table */
case class UserPasswordTable[P <:JdbcProfile]()(implicit val driver: P)
  extends Table[UserPassword, P] with DBConfig {
  import api._

  /** Definition of data source name */
  lazy val dsn = Map(
    "master" -> DataSourceName("ixias.db.mysql://master/" + databaseName),
    "slave"  -> DataSourceName("ixias.db.mysql://slave/"  + databaseName)
  )

  /** Definition of query */
  class Query extends BasicQuery(new Table(_)) {}
  lazy val query = new Query

  /** Definition of table */
  class Table(tag: Tag) extends BasicTable(tag, userPasswordTableName){

    /** Column mapping */
    /* @1 */ def id        = column[User.Id]       ("user_id",    O.UInt64, O.PrimaryKey)
    /* @2 */ def hash      = column[String]        ("hash",       O.Utf8Char255)
    /* @3 */ def updatedAt = column[LocalDateTime] ("updated_at", O.TsCurrent)
    /* @4 */ def createdAt = column[LocalDateTime] ("created_at", O.Ts)

    type TableElementTuple = (
      Option[User.Id], String, LocalDateTime, LocalDateTime
    )

    /** Bidirectional mapping between DB and Scala */
    def * = (id.?, hash, updatedAt, createdAt) <> (
      (UserPassword.apply _).tupled,
      (UserPassword.unapply _).andThen(_.map(_.copy(
        _3 = LocalDateTime.now()
      )))
    )
  }
}

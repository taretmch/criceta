package criceta.play.auth.ixias.persistence.db

import java.time.LocalDateTime
import slick.jdbc.JdbcProfile

import ixias.persistence.model.Table
import criceta.play.auth.ixias.model.User

/** UserTable: Mapping between User model and MySQL table */
case class UserTable[P <:JdbcProfile]()(implicit val driver: P)
  extends Table[User, P] with DBConfig {
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
  class Table(tag: Tag) extends BasicTable(tag, userTableName){
    import User._

    /** Column mapping */
    /* @1 */   def id            = column[User.Id]       ("id",         O.UInt64, O.PrimaryKey, O.AutoInc)
    /* @2 */   def email         = column[String]        ("email",      O.AsciiChar255)
    /* @3 */   def updatedAt     = column[LocalDateTime] ("updated_at", O.TsCurrent)
    /* @4 */   def createdAt     = column[LocalDateTime] ("created_at", O.Ts)

    type TableElementTuple = (
      Option[User.Id], EmailAddress, LocalDateTime, LocalDateTime
    )

    /** Bidirectional mapping between DB and Scala */
    def * = (id.?, email, updatedAt, createdAt) <> (
      (User.apply _).tupled.compose(t => t.copy(
        _2 = EmailAddress(t._2)
      )),
      (User.unapply _).andThen(_.map(t => t.copy(
        _2 = t._2.email,
        _3 = LocalDateTime.now()
      )))
    )
  }
}

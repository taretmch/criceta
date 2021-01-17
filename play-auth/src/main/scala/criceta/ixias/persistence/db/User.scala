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
    /* @1 */   def id            = column[User.Id]       ("id",              O.UInt64, O.PrimaryKey, O.AutoInc)
    /* @2.1 */ def nameLast      = column[String]        ("name_last",       O.AsciiChar64)
    /* @2.2 */ def nameFirst     = column[String]        ("name_first",      O.AsciiChar64)
    /* @2.3 */ def nameKanaLast  = column[String]        ("name_kana_last",  O.AsciiChar64)
    /* @2.4 */ def nameKanaFirst = column[String]        ("name_kana_first", O.AsciiChar64)
    /* @3 */   def email         = column[String]        ("email",           O.AsciiChar255)
    /* @4 */   def updatedAt     = column[LocalDateTime] ("updated_at",      O.TsCurrent)
    /* @5 */   def createdAt     = column[LocalDateTime] ("created_at",      O.Ts)

    type TableElementTuple = (
      Option[User.Id], Name, EmailAddress, LocalDateTime, LocalDateTime
    )

    /** Bidirectional mapping between DB and Scala */
    def * = (id.?, (nameLast, nameFirst, nameKanaLast, nameKanaFirst), email, updatedAt, createdAt) <> (
      (User.apply _).tupled.compose(t => t.copy(
        _2 = Name(t._2._1, t._2._2, t._2._3, t._2._4),
        _3 = EmailAddress(t._3)
      )),
      (User.unapply _).andThen(_.map(t => t.copy(
        _2 = (t._2.last, t._2.first, t._2.kanaLast, t._2.kanaFirst),
        _3 = t._3.email,
        _4 = LocalDateTime.now()
      )))
    )
  }
}

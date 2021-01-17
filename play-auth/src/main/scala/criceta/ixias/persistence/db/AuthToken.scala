package criceta.play.auth.ixias.persistence.db

import java.time.{ Duration, LocalDateTime }
import slick.jdbc.JdbcProfile

import ixias.persistence.model.Table
import ixias.play.api.auth.token.Token.AuthenticityToken 
import criceta.play.auth.ixias.model.{ AuthToken, User }

/** AuthTokenTable: Mapping between AuthToken model and MySQL table */
case class AuthTokenTable[P <:JdbcProfile]()(implicit val driver: P)
  extends Table[AuthToken, P] with DBConfig {
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
  class Table(tag: Tag) extends BasicTable(tag, authTokenTableName){

    /** Column mapping */
    /* @1 */ def id        = column[AuthToken.Id]      ("id",         O.UInt64, O.PrimaryKey, O.AutoInc)
    /* @2 */ def uid       = column[User.Id]           ("user_id",    O.UInt64)
    /* @3 */ def token     = column[AuthenticityToken] ("token",      O.Utf8Char255)
    /* @4 */ def expiry    = column[Option[Duration]]  ("expiry",     O.Utf8Char255)
    /* @5 */ def updatedAt = column[LocalDateTime]     ("updated_at", O.TsCurrent)
    /* @6 */ def createdAt = column[LocalDateTime]     ("created_at", O.Ts)

    type TableElementTuple = (
      Option[AuthToken.Id], User.Id, AuthenticityToken, Option[Duration], LocalDateTime, LocalDateTime
    )

    /** Bidirectional mapping between DB and Scala */
    def * = (id.?, uid, token, expiry, updatedAt, createdAt) <> (
      (AuthToken.apply _).tupled,
      (AuthToken.unapply _).andThen(_.map(_.copy(
        _5 = LocalDateTime.now
      )))
    )
  }
}

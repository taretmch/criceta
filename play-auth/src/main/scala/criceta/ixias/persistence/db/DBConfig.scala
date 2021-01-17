package criceta.play.auth.ixias.persistence.db

import ixias.util.Configuration

/** Configuration of database for auth */
trait DBConfig {

  /** The configuration */
  protected val config = Configuration()

  /** The keys of configuration */
  protected val CF_DSN_PATH                 = "criceta.db.mysql"
  protected val CF_DATABASE_NAME            = "database_name"
  protected val CF_USER_TABLE_NAME          = "user_table_name"
  protected val CF_USER_PASSWORD_TABLE_NAME = "user_password_table_name"
  protected val CF_AUTH_TOKEN_TABLE_NAME    = "auth_token_table_name"

  /** Get name of database */
  lazy val databaseName: String =
    readValue(_.get[Option[String]](CF_USER_TABLE_NAME))
      .getOrElse("criceta_udb")

  /** Get name of user table */
  lazy val userTableName: String =
    readValue(_.get[Option[String]](CF_USER_TABLE_NAME))
      .getOrElse("user")

  /** Get name of user password table */
  lazy val userPasswordTableName: String =
    readValue(_.get[Option[String]](CF_USER_PASSWORD_TABLE_NAME))
      .getOrElse("user_password")

  /** Get name of auth token table */
  lazy val authTokenTableName: String =
    readValue(_.get[Option[String]](CF_AUTH_TOKEN_TABLE_NAME))
      .getOrElse("auth_token")

  /**  Get a value by specified key */
  def readValue[A](f: Configuration => Option[A]): Option[A] =
    f(config.get[Configuration](CF_DSN_PATH))
}

package criceta.play.auth.ixias.persistence.db

import slick.jdbc.JdbcProfile

/** Resource provider */
trait SlickResourceProvider[P <: JdbcProfile] {

  implicit val driver: P

  object UserTable         extends UserTable
  object UserPasswordTable extends UserPasswordTable
  object AuthTokenTable    extends AuthTokenTable

  lazy val AllTables = Seq(
    UserTable,
    UserPasswordTable,
    AuthTokenTable,
  )
}

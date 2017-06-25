package sql.drivers

import com.github.tminglei.slickpg.{PgSearchSupport, _}
import slick.jdbc.PostgresProfile

/**
  * Created by caeus on 24/06/17.
  */
trait ExtendedPGProfile extends PostgresProfile
  with PgSearchSupport
  with PgArraySupport {

  override val api = ExtendedAPI

  object ExtendedAPI extends API
    with ArrayImplicits
    with SearchImplicits
    with SearchAssistants {
    implicit val strListTypeMapper = new SimpleArrayJdbcType[String]("text").to(_.toList)
  }

}

object ExtendedPGProfile extends ExtendedPGProfile


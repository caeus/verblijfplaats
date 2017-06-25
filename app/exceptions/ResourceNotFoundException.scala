package exceptions
import play.api.libs.json.{JsNull, JsValue}

/**
  * Created by caeus on 25/06/17.
  */
case class ResourceNotFoundException(message:String) extends Exception(message) with ManagedException{
  override val httpStatus: Int = 404
  override val details: JsValue = JsNull
}

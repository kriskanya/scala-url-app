package controllers

import javax.inject.Inject

import models.Widget
import play.api.data._
import play.api.i18n._
import play.api.mvc._

import scala.io.Source
import scala.collection.mutable.ArrayBuffer

/**
 * The classic WidgetController using MessagesAbstractController.
 *
 * Instead of MessagesAbstractController, you can use the I18nSupport trait,
 * which provides implicits that create a Messages instance from a request
 * using implicit conversion.
 *
 * See https://www.playframework.com/documentation/2.6.x/ScalaForms#passing-messagesprovider-to-form-helpers
 * for details.
 */
class WidgetController @Inject()(cc: MessagesControllerComponents) extends MessagesAbstractController(cc) {
  import WidgetForm._

  private val widgets = ArrayBuffer[Widget]()

  // The URL to the widget.  You can call this directly from the template, but it
  // can be more convenient to leave the template completely stateless i.e. all
  // of the "WidgetController" references are inside the .scala file.
  private val postUrl = routes.WidgetController.createWidget()

  def index = Action {
    Ok(views.html.index())
  }

  def listWidgets = Action { implicit request: MessagesRequest[AnyContent] =>
    // Pass an unpopulated form to the template
    Ok(views.html.listWidgets(widgets, form, postUrl))
  }

  def clearList = Action { implicit request: MessagesRequest[AnyContent] =>
    widgets.clear()
    Redirect(routes.WidgetController.listWidgets()).flashing("alert" -> "List cleared")
  }

  // This will be the action that handles our form post
  def createWidget = Action { implicit request: MessagesRequest[AnyContent] =>
    val errorFunction = { formWithErrors: Form[Data] =>
      // This is the bad case, where the form had validation errors.
      // Let's show the user the form again, with the errors highlighted.
      // Note how we pass the form with errors to the template.
      BadRequest(views.html.listWidgets(widgets, formWithErrors, postUrl))
    }

    val successFunction = { data: Data =>
      // This is the good case, where the form was successfully parsed as a Data object.
      // http://docs.sequelizejs.com

      try {
        val res = scala.io.Source.fromURL(data.url)("ISO-8859-1").mkString
        val pattern = "(?<=<title>)(.*?)(?=</title>)".r 
        val match1 = pattern.findFirstIn(res)
        val s = match1.map(_.toString).getOrElse("")

        if(s == "") {
          Redirect(routes.WidgetController.listWidgets()).flashing("alert" -> "Title is empty. Consider adding http or https.")
        } else {
          val widget = Widget(url = s)
          widgets.append(widget)
          Redirect(routes.WidgetController.listWidgets()).flashing("alert" -> "Site title added!")
        }
      } catch {
        case e: Exception => Redirect(routes.WidgetController.listWidgets()).flashing("alert" -> "Some error occurred.")
      }
      
    }

    val formValidationResult = form.bindFromRequest
    formValidationResult.fold(errorFunction, successFunction)
  }
}

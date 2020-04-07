package controllers

import javax.inject._
import models.{Registration, User}
import play.api._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class LoginController @Inject()(val components: ControllerComponents, val mongoService: MongoService) extends AbstractController(components) with play.api.i18n.I18nSupport {

  implicit def ec: ExecutionContext = components.executionContext

  def logout():Action[AnyContent] = Action {implicit request: Request[AnyContent] =>
    Redirect(routes.HomeController.index()).withNewSession
  }
}
package controllers

import javax.inject._
import models.{Registration, User}
import play.api._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RegistrationController @Inject()(val components: ControllerComponents, val mongoService: MongoService) extends AbstractController(components) with play.api.i18n.I18nSupport{

  implicit def ec: ExecutionContext = components.executionContext

  def addUser(username: String, email: String, password: String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val user = User(username, email, password)
    mongoService.addUser(user)
    Future(Redirect(routes.HomeController.index()).withSession(request.session + ("user", user.username)))
  }

  def showRegistration(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.registration(Registration.RegistrationForm))
  }

  def submitRegistration(): Action[AnyContent] = Action {implicit request: Request[AnyContent] =>
    Registration.RegistrationForm.bindFromRequest.fold({ formWithErrors =>
      BadRequest(views.html.registration(formWithErrors))
    }, { register =>
      Redirect(routes.RegistrationController.addUser(register.username, register.email, register.password))

    })
  }

}
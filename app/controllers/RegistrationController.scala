package controllers

import javax.inject._
import models.{Registration, User}
import play.api._
import play.api.mvc._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

@Singleton
class RegistrationController @Inject()(val components: ControllerComponents, val mongoService: MongoService) extends AbstractController(components) with play.api.i18n.I18nSupport{

  implicit def ec: ExecutionContext = components.executionContext

  def reInnit(): Action[AnyContent] = Action.async { implicit request:Request[AnyContent] =>
    mongoService.reInnitUsers.map(_ => Ok("Reinitialised collection with admin user"))
  }

  def addUser(username: String, email: String, password: String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    var err = ""
    if (Await.result(mongoService.userEmailExists(email), Duration.Inf)) {
      err = "Email is already in use, please choose another or log in instead"
      Future(Redirect(routes.RegistrationController.showRegistration(err)))
    }
    else if (Await.result(mongoService.userUsernameExists(username), Duration.Inf)) {
      err = "Username is already in use, please select another"
      Future(Redirect(routes.RegistrationController.showRegistration(err)))
    }
    else {
      val user = User(username, email, password)
      mongoService.addUser(user)
      Future(Redirect(routes.HomeController.index()).withSession(request.session + ("user", user.username)))
    }
  }

  def showRegistration(err: String = ""): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.registration(Registration.RegistrationForm, err))
  }

  def submitRegistration(): Action[AnyContent] = Action {implicit request: Request[AnyContent] =>
    Registration.RegistrationForm.bindFromRequest.fold({ formWithErrors =>
      BadRequest(views.html.registration(formWithErrors, ""))
    }, { register =>
      Redirect(routes.RegistrationController.addUser(register.username, register.email, register.password))

    })
  }

}
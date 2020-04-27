package controllers

import javax.inject._
import views._
import models.{Login, Registration, User}
import org.mindrot.jbcrypt.BCrypt
import play.api._
import play.api.mvc._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

@Singleton
class LoginController @Inject()(val components: ControllerComponents, val mongoService: MongoService) extends AbstractController(components) with play.api.i18n.I18nSupport {

  implicit def ec: ExecutionContext = components.executionContext

  def logout():Action[AnyContent] = Action {implicit request: Request[AnyContent] =>
    Redirect(routes.HomeController.index()).withNewSession
  }

  def showLogin(err: String = ""): Action[AnyContent] = Action {implicit request: Request[AnyContent] =>
    Ok(views.html.login(Login.LoginForm))
  }

  def submitLogin(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>

    Login.LoginForm.bindFromRequest.fold({ formWithErrors =>
      BadRequest(views.html.login(formWithErrors))
    }, { login =>
      if (mongoService.isEmail(login.account)) {
        lazy val email = Await.result(mongoService.findUserEmail(login.account), Duration.Inf)
        if (email.isDefined && BCrypt.checkpw(login.password, Await.result(mongoService.findUserEmail(login.account), Duration.Inf).head.password)) {
          Redirect(routes.HomeController.index()).withSession("user" -> email.get.username)
        }
        else {
          Redirect(routes.LoginController.showLogin()).withSession("loginError" -> "error.emailIncorrect")
        }
      }
      else {
        lazy val username = Await.result(mongoService.findUserUsername(login.account), Duration.Inf)
        if (username.isDefined && BCrypt.checkpw(login.password, Await.result(mongoService.findUserUsername(login.account), Duration.Inf).head.password)) {
          Redirect(routes.HomeController.index()).withSession("user" -> username.get.username)
        }
        else {
          Redirect(routes.LoginController.showLogin()).withSession("loginError" -> "error.userIncorrect")
        }
      }
      }
    )
  }
}
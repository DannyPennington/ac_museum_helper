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
    Ok(views.html.login(Login.LoginForm, err))
  }

  def submitLogin(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    var err = ""
    Login.LoginForm.bindFromRequest.fold({ formWithErrors =>
      BadRequest(views.html.login(formWithErrors, ""))
    }, { login =>
      val user = Await.result(mongoService.findUserEmail(login.email), Duration.Inf)

      if ( user.isDefined && BCrypt.checkpw(login.password,Await.result(mongoService.findUserEmail(login.email), Duration.Inf).head.password)) {
        Redirect(routes.HomeController.index()).withSession("user" -> user.get.username)
      }
    else {
      err = "Email address or password was incorrect"
      Redirect(routes.LoginController.showLogin(err))
    }
    }
    )
  }
}
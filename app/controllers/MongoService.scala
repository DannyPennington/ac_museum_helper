
package controllers

import javax.inject.Inject
import models._
import play.api.mvc._
import reactivemongo.play.json.collection.{JSONCollection, _}

import scala.concurrent.{ExecutionContext, Future}
import reactivemongo.play.json._
import collection._
import models.JsonFormats._
import play.api.libs.json.{JsValue, Json}
import reactivemongo.api.Cursor
import play.modules.reactivemongo.{ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.commands.WriteResult
import scala.concurrent.ExecutionContext.Implicits.global


class MongoService @Inject()(
                              val reactiveMongoApi: ReactiveMongoApi
                            ) extends ReactiveMongoComponents {

  def userCollection: Future[JSONCollection] = reactiveMongoApi.database.map(_.collection[JSONCollection]("users"))

  def userSearchHelper(category: String = "email", search: String): Future[List[User]] = {
    userCollection.map {
        _.find(Json.obj(category -> search))
        .sort(Json.obj(category -> -1))
        .cursor[User]()
    }.flatMap(
      _.collect[List](
        1,
        Cursor.FailOnError[List[User]]()
      )
    )
  }

  def reInnitUsers: Future[WriteResult] = {
    userCollection.map {
      _.drop
    }
    addUser(User("admin", "admin@admin.com", "admin"))
  }

  def addUser(user: User): Future[WriteResult] = {
    userCollection.flatMap(_.insert.one(user))
  }

  def findUserEmail(email: String): Future[Option[User]] = {
    userSearchHelper("email",email)
      .map(_.headOption)
  }

  def findUserUsername(username: String):Future[Option[User]] ={
    userSearchHelper("username", username)
      .map(_.headOption)
  }

  def userEmailExists(email: String): Future[Boolean] = {
    findUserEmail(email).map {
      _.getOrElse(None)
      match {
        case None => false
        case _ => true
      }
    }
  }
  def userUsernameExists(username: String): Future[Boolean] = {
    findUserUsername(username).map {
      _.getOrElse(None)
      match {
        case None => false
        case _ => true
      }
    }
  }
}

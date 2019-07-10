package org.november.learning.akka.stream

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source

import scala.concurrent.Future

object Main extends  App {

  implicit val system = ActorSystem.create("Learning-AKKA-Streams")
  implicit val mat = ActorMaterializer.create(system)
  /*
  The Source type is parameterized with two types: the first one is the type of element that this source emits
  and the second one may signal that running the source produces some auxiliary value
  (e.g. a network source may provide information about the bound port or the peer’s address).
  Where no auxiliary information is produced, the type akka.NotUsed is used—and a simple range of integers
  surely falls into this category.
   */
  val source: Source[Int, NotUsed] = Source(1 to 100)

  /*
  This line will complement the source with a consumer function. This activation is signaled by having “run”
  be part of the method name
  */
  source.runForeach(i => println(i))

  /*
  runForeach returns a Future[Done] which resolves when the streams finishes.  To terminate the app you need
  to terminate the actor system upon completion of the future
   */
  val done: Future[Done] = source.runForeach(i => println(i))(mat)

  implicit val ec = system.dispatcher
  done.onComplete(_ => system.terminate())

}

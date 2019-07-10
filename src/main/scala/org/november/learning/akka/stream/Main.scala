package org.november.learning.akka.stream

import java.nio.file.Paths

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, IOResult}
import akka.stream.scaladsl.{FileIO, Source}
import akka.util.ByteString

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

  //#Start example #2
  //There is nothing computed yet, this is a description of what we want to have computed once we run the stream.
  val factorials = source.scan(BigInt(1))((acc, next) => acc * next)

  /*The elements in the source is access to be converted to ByteStrin using the map function.
  his stream is then run by attaching a file as the receiver of the data. In the terminology of Akka Streams this
   is called a Sink. IOResult is a type that IO operations return in Akka Streams in order to tell you how many
   bytes or elements were consumed and whether the stream terminated normally or exceptionally.
  */
  val result: Future[IOResult] =
    factorials.map(num => ByteString(s"$num\n")).runWith(FileIO.toPath(Paths.get("factorials.txt")))
  //#End example #2


  implicit val ec = system.dispatcher
  result.onComplete(_ => system.terminate())

}

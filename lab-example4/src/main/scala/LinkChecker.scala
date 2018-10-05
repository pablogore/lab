import Getter.Done
import akka.actor.{Actor, ActorRef, Props}

object LinkChecker {
  case class CheckUrl(url: String, depth: Int){}
  case class Result(url: String, links: Set[String]) {}
}

class LinkChecker(root: String, originalDepth: Integer) extends Actor {
  import LinkChecker._
  var cache = Set.empty[String]
  var children = Set.empty[ActorRef]

  self ! CheckUrl(root,originalDepth)

  override def receive: Receive = {
    case CheckUrl(url,depth) =>
      if (!cache(url) && depth > 0)
        children += context.actorOf(Props[Getter](new Getter(url, depth-1)))
      cache+=url

    case Done =>
      children-=sender
      if (children.isEmpty ) context.parent ! Result(root,cache)
  }
}

package co.adhoclabs.template.api

import akka.http.scaladsl.server.Route
import org.slf4j.Logger

trait ApiBase extends JsonSupport {
  val routes: Route
  protected val logger: Logger
}
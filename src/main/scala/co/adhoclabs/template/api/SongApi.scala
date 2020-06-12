package co.adhoclabs.template.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import co.adhoclabs.template.business.SongManager
import co.adhoclabs.template.models.{CreateSongRequest, Song}
import java.util.UUID
import org.slf4j.{Logger, LoggerFactory}

trait SongApi extends ApiBase {
  val routes: Route
}

class SongApiImpl(implicit songManager: SongManager) extends SongApi {

  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  override val routes: Route = pathPrefix("songs") {
    concat(
      pathEnd {
        post {
          postSong
        }
      },
      path(JavaUUID) { id: UUID =>
        concat(
          get {
            getSong(id)
          },
          put {
            putSong(id)
          }
        )
      }
    )
  }

  def getSong(id: UUID): Route =
    rejectEmptyResponse {
      complete {
        songManager.get(id)
      }
    }

  def postSong: Route = entity(as[CreateSongRequest]) { songRequest: CreateSongRequest =>
    complete {
      StatusCodes.Created -> songManager.create(songRequest)
    }
  }

  def putSong(id: UUID): Route = entity(as[Song]) { song: Song =>
    rejectEmptyResponse {
      complete {
        songManager.update(song)
      }
    }
  }
}
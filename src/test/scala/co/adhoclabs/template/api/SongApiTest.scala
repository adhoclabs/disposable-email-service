package co.adhoclabs.template.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import co.adhoclabs.template.models.Song
import scala.concurrent.Future

class SongApiTest extends ApiTestBase {

  describe("GET /songs/:id") {
    it("should call SongManager.get") {
      val expectedSong: Song = Song(
        id = "song-id-123",
        title = "Once in a Lifetime",
        album = "Remain in Light",
        albumPosition = 1
      )

      (songManager.get _)
        .expects(expectedSong.id)
        .returning(Future.successful(Some(expectedSong)))

      Get(s"/songs/${expectedSong.id}") ~> Route.seal(routes) ~> check {
        assert(status == StatusCodes.OK)
        assert(responseAs[Song] == expectedSong)
      }
    }
  }

}

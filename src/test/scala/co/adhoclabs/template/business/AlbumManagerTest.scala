package co.adhoclabs.template.business

import co.adhoclabs.analytics.AnalyticsManager
import co.adhoclabs.template.analytics.AlbumCreatedAnalyticsEvent
import co.adhoclabs.template.data.AlbumDao
import co.adhoclabs.template.exceptions.{AlbumAlreadyExistsException, NoSongsInAlbumException}
import co.adhoclabs.template.models._
import scala.concurrent.Future

class AlbumManagerTest extends BusinessTestBase {
  implicit val albumDao: AlbumDao = mock[AlbumDao]
  implicit val analyticsManager: AnalyticsManager = mock[AnalyticsManager]

  val albumManager: AlbumManager = new AlbumManagerImpl

  describe("get") {
    val expectedAlbumWithSongs = generateAlbumWithSongs()

    it("should return a album with the supplied id") {
      (albumDao.get _)
        .expects(expectedAlbumWithSongs.album.id)
        .returning(Future.successful(Some(expectedAlbumWithSongs)))

      albumManager.get(expectedAlbumWithSongs.album.id) map {
        case Some(albumWithSongs) => assert(albumWithSongs == expectedAlbumWithSongs)
        case None => fail
      }
    }

    it("should return None when the album doesn't exist") {
      (albumDao.get _)
          .expects(expectedAlbumWithSongs.album.id)
          .returning(Future.successful(None))

      albumManager.get(expectedAlbumWithSongs.album.id) flatMap {
        case None => succeed
        case Some(_) => fail
      }
    }
  }

  describe("create") {
    val expectedAlbumWithSongs = generateAlbumWithSongs()

    val createAlbumRequest = CreateAlbumRequest(
      title = expectedAlbumWithSongs.album.title,
      genre = expectedAlbumWithSongs.album.genre,
      songs = expectedAlbumWithSongs.songs.map(_.title),
    )

    it("should call AlbumDao.create and return an album when successful"){
      (albumDao.create _)
          .expects(*)
          .returning(Future.successful(expectedAlbumWithSongs))

      (analyticsManager.trackEvent _)
        .expects(AlbumCreatedAnalyticsEvent(expectedAlbumWithSongs.album))
        .returning(Future.successful())

      albumManager.create(createAlbumRequest) map { albumWithSongs: AlbumWithSongs =>
        assert(albumWithSongs == expectedAlbumWithSongs)
      }
    }

    it("should call AlbumDao.create and throw an exception for an album with no songs") {
      recoverToSucceededIf[NoSongsInAlbumException] {
        albumManager.create(createAlbumRequest.copy(songs = List.empty[String]))
      }
    }

    it("should throw an exception from AlbumDao.create when attempting to create an album with an ID that already exists") {
      (albumDao.create _)
          .expects(*)
          .returning(Future.failed(AlbumAlreadyExistsException("album already exists")))

      recoverToSucceededIf[AlbumAlreadyExistsException] {
        albumManager.create(createAlbumRequest)
      }
    }
  }
  
  describe("update") {
    val expectedAlbum = generateAlbum()

    it("should call AlbumDao.update and return updated album if it already exists") {
      (albumDao.update _)
          .expects(expectedAlbum)
          .returning(Future.successful(Some(expectedAlbum)))

      albumManager.update(expectedAlbum) map {
        case Some(updatedAlbum: Album) => assert(updatedAlbum == expectedAlbum)
        case None => fail
      }
    }

    it("should call albumDao.update and return None if the album doesn't exist") {
      (albumDao.update _)
          .expects(*)
          .returning(Future.successful(None))

      albumManager.update(expectedAlbum) map {
        case None => succeed
        case Some(_: Album) => fail
      }
    }
  }

  describe("delete") {
    val expectedAlbumWithSongs = generateAlbumWithSongs()

    it("should return Unit if the album was deleted") {
      (albumDao.delete _)
        .expects(expectedAlbumWithSongs.album.id)
        .returning(Future.successful(1))

      albumManager.delete(expectedAlbumWithSongs.album.id) map { u =>
        assert(u == ())
      }
    }

    it("should return Unit if the album doesn't exist") {
      (albumDao.delete _)
        .expects(expectedAlbumWithSongs.album.id)
        .returning(Future.successful(0))

      albumManager.delete(expectedAlbumWithSongs.album.id) map { u =>
        assert(u == ())
      }
    }
  }
}

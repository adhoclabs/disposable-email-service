package co.adhoclabs.email.business

import co.adhoclabs.email.data.SchemaHistoryDao

import scala.concurrent.{ExecutionContext, Future}

trait HealthManager {
//  def executeDbGet(): Future[Unit]
}

class HealthManagerImpl(
  implicit
//  schemaHistoryDao: SchemaHistoryDao,
  executionContext: ExecutionContext
) extends HealthManager {
//  override def executeDbGet(): Future[Unit] = {
//    schemaHistoryDao.getLatest().map(_ => ())
//  }
}

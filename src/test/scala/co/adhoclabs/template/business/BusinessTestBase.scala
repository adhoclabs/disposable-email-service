package co.adhoclabs.template.business

import com.typesafe.config.ConfigFactory
import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.funspec.AsyncFunSpec

abstract class BusinessTestBase extends AsyncFunSpec with AsyncMockFactory {
  implicit protected val config = ConfigFactory.load()
}

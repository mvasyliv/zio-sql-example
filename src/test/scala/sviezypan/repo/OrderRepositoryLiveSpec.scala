package sviezypan.repo

import zio._
import zio.test._
import zio.test.Assertion._
import sviezypan.domain.Order
import zio.test.TestAspect._
import java.util.UUID
import java.time.LocalDate
import sviezypan.repo.postgresql.PostgresContainer

object OrderRepositoryLiveSpec extends DefaultRunnableSpec {

  val testLayer = ZLayer.make[OrderRepository](
    OrderRepositoryLive.layer,
    PostgresContainer.connectionPoolConfigLayer,
    PostgresContainer.make()
  )

  val order = Order(UUID.randomUUID(), UUID.randomUUID(), LocalDate.now())

  def spec = 
    suite("order repository test with postgres test container")(
      test("count all orders") {
        for {
          count <- OrderRepository.countAllOrders()
        } yield assert(count)(equalTo(25))
      },
      test("insert new order") {
        for {
          _ <- OrderRepository.add(order)
          count <- OrderRepository.countAllOrders()
        } yield assert(count)(equalTo(26))
      }
    ).provideCustomLayerShared(testLayer.orDie) @@ sequential
}
package com.awkiamaru.entities

import com.awkiamaru.controllers.Request
import com.awkiamaru.entities.OrderStatus.CANCELED
import com.awkiamaru.entities.OrderStatus.CONFIRMED
import com.awkiamaru.entities.OrderStatus.PENDING
import com.awkiamaru.events.OrderEvent
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.MappedProperty
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.DataType
import io.micronaut.data.model.query.builder.sql.Dialect.POSTGRES
import io.micronaut.data.repository.CrudRepository
import io.micronaut.serde.annotation.Serdeable
import java.util.UUID

@Serdeable
@MappedEntity("customer_order")
data class OrderEntity(
  @field:Id
  var id: UUID = UUID.randomUUID(),
  val customerId: UUID,

  @field:MappedProperty(definition = "status",type = DataType.OBJECT)
  val status: OrderStatus
) {
  fun toOrdered(products: List<Request.OrderItem>) = OrderEvent.Ordered(
      id = id,
      customerId = customerId,
      items = products,
      status = status
    )

  fun confirm() = copy(status = CONFIRMED)

  fun cancel() = copy(status = CANCELED)

  companion object {
    fun create(customerId: UUID) = OrderEntity(customerId = customerId, status = PENDING)
  }
}

@JdbcRepository(dialect = POSTGRES)
interface OrderRepository: CrudRepository<OrderEntity, UUID>


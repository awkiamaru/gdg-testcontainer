package com.awkiamaru.entities

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect.POSTGRES
import io.micronaut.data.repository.CrudRepository
import io.micronaut.serde.annotation.Serdeable
import java.util.UUID

@Serdeable
@MappedEntity("order_item")
data class OrderItemEntity(
  @field:Id
  var id: UUID = UUID.randomUUID(),
  val orderId: UUID,
  val productId: UUID,
  val quantity: Int
) {
  companion object {
    fun create(
      orderId: UUID,
      productId: UUID,
      quantity: Int
    ) = OrderItemEntity(
      orderId = orderId,
      productId = productId,
      quantity = quantity
    )
  }
}

@JdbcRepository(dialect = POSTGRES)
interface OrderItemRepository: CrudRepository<OrderItemEntity, UUID>
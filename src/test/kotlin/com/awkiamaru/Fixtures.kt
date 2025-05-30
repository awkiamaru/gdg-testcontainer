package com.awkiamaru

import com.awkiamaru.controllers.Request
import com.awkiamaru.entities.CustomerEntity
import com.awkiamaru.entities.OrderEntity
import com.awkiamaru.entities.OrderItemEntity
import com.awkiamaru.entities.OrderStatus
import com.awkiamaru.entities.ProductEntity
import io.kotest.property.Arb
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.uuid
import java.util.UUID

object Fixtures {
  fun requestProduct(
    name: String = Arb.string().single(),
    stock: Int = Arb.int(1..3).single()
  ) = Request.Product(name = name, stock = stock)

  fun requestCustomer(name: String = Arb.string().single()) = Request.Customer(name = name)

  fun requestOrder(
    customerId: UUID = Arb.uuid().single(),
    items: List<Request.OrderItem>
  ) = Request.Order(customerId = customerId, products = items)


  fun product(
    id: UUID = Arb.uuid().single(),
    name: String = Arb.string().single(),
    stock: Int = Arb.int(1..3).single()
  ) = ProductEntity(id = id, name = name, stock = stock)

  fun customer(
    id: UUID = Arb.uuid().single(),
    name: String = Arb.string().single(),
  ) = CustomerEntity(id = id, name = name)

  fun order(
    id: UUID = Arb.uuid().single(),
    customerId: UUID = Arb.uuid().single(),
    status: OrderStatus = Arb.enum<OrderStatus>().single(),
  ) = OrderEntity(id = id, customerId = customerId, status = status)

  fun orderItem(
    id: UUID = Arb.uuid().single(),
    productId: UUID = Arb.uuid().single(),
    orderId: UUID = Arb.uuid().single(),
    quantity: Int = Arb.int(1..3).single()
  ) = OrderItemEntity(id = id, orderId = orderId, productId = productId, quantity = quantity)
}
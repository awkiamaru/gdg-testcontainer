package com.awkiamaru.controllers

import com.awkiamaru.entities.CustomerRepository
import com.awkiamaru.entities.OrderEntity
import com.awkiamaru.entities.OrderItemEntity
import com.awkiamaru.entities.OrderItemRepository
import com.awkiamaru.entities.OrderRepository
import com.awkiamaru.entities.ProductRepository
import com.awkiamaru.events.OrderProducer
import io.micronaut.http.HttpStatus.CREATED
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Status
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

@Controller("/orders")
class OrderController(
  private val orderRepository: OrderRepository,
  private val orderProducer: OrderProducer,
  private val customerRepository: CustomerRepository,
  private val productRepository: ProductRepository
) {

  @Post
  @Status(CREATED)
  fun create(@Body body: Request.Order): OrderEntity = with(body){
    val orderedProducts = products.associateBy { it.id }

    customerRepository
      .findById(customerId)
      .getOrNull()
      ?.let {
        val products = productRepository.findByIdIn(orderedProducts.keys.toList()).associateBy { it.id }
        matchOrderedProduct(orderedProducts.keys, products.keys)

        return OrderEntity.create(customerId)
          .run(orderRepository::save)
          .also { orderProducer.produce(it.toOrdered(body.products)) }

      }
      ?: throw Exception("Customer $customerId not found.")
  }

  @Get("/{orderId}")
  fun findById(orderId: UUID) = orderRepository.findById(orderId)

  private fun matchOrderedProduct(
    ordered: Set<UUID>,
    found: Set<UUID>) {
    ordered
      .takeIf { it.isEmpty() }
      ?.let { throw Exception("Order must have one selected product.") }

     (ordered - found)
      .takeIf { it.isNotEmpty() }
      ?.let { throw Exception("Not found products: $it") }
}
}
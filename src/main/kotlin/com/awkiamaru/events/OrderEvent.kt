package com.awkiamaru.events

import com.awkiamaru.controllers.Request
import com.awkiamaru.entities.OrderItemEntity
import com.awkiamaru.entities.OrderItemRepository
import com.awkiamaru.entities.OrderRepository
import com.awkiamaru.entities.OrderStatus
import com.awkiamaru.entities.ProductEntity
import com.awkiamaru.entities.ProductRepository
import io.micronaut.configuration.kafka.annotation.KafkaKey
import io.micronaut.configuration.kafka.annotation.KafkaListener
import io.micronaut.configuration.kafka.annotation.Topic
import io.micronaut.messaging.annotation.MessageBody
import io.micronaut.serde.annotation.Serdeable
import jakarta.inject.Singleton
import java.util.UUID
import kotlin.jvm.optionals.getOrElse
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory

sealed class OrderEvent {

  @Serdeable
  data class Ordered(val id: UUID, val customerId: UUID, val status: OrderStatus, val items: List<Request.OrderItem>)

  companion object {
    const val ORDER_TOPIC = "tp_order"
    const val CG_ORDER = "cg_order"
  }
}


@Singleton
class OrderProducer(
  private val producer: Producer<UUID, OrderEvent.Ordered>
) {
  private val logger: Logger = LoggerFactory.getLogger(OrderProducer::class.java)

  fun produce(event: OrderEvent.Ordered) {
    ProducerRecord(OrderEvent.ORDER_TOPIC, null, event.id, event)
      .let { producer.send(it).get() }
      .also {
        logger.info(
          "Order created: { id: {}, status: {}, customer: {}, topic: {} }",
          event.id, event.status, event.customerId, it.topic()
        )
      }
  }

}

@KafkaListener(
  groupId = OrderEvent.CG_ORDER,
  clientId = OrderEvent.CG_ORDER
)
class OrderConsumer(
  private val orderRepository: OrderRepository,
  private val orderItemRepository: OrderItemRepository,
  private val productsRepository: ProductRepository
) {
  private val logger: Logger = LoggerFactory.getLogger(OrderConsumer::class.java)

  @Topic(OrderEvent.ORDER_TOPIC)
  fun consume(
    @KafkaKey key: String,
    @MessageBody body: OrderEvent.Ordered
  ) {
    val order = getOrder(body.id)
    val orderedItems = body.items.associateBy { it.id }
    val productsById = productsRepository.findByIdIn(orderedItems.keys.toList()).associateBy { it.id }


    logger.info("Updating product stocks: { orderId: {}, customerId: {} }", order.id, order.customerId)

    runCatching {
      updateProductsInStock(order.id, orderedItems, productsById)
      .values
      .toList()
      .let { productsRepository.updateAll(it) }

      orderedItems
        .values
        .toList()
        .map { OrderItemEntity.create(orderId = order.id, productId = it.id, quantity = it.quantity) }
        .let { orderItemRepository.saveAll(it) }
        .also { order.confirm().run(orderRepository::update) }

    }.getOrElse { order.cancel().run(orderRepository::update) }




  }

  private fun updateProductsInStock(
    orderId: UUID,
    requested: Map<UUID, Request.OrderItem>,
    available: Map<UUID, ProductEntity>
  ): Map<UUID, ProductEntity> {
    val missing = requested.filter { (id, request) ->
      val stock = available[id]?.stock
      stock == null || stock < request.quantity
    }

    if (missing.isNotEmpty()) {
      missing
        .map { (key, value) ->
        logger.warn("Product insufficient stock: { order: {}, product: {}, requested: {}, available: {} }",
            orderId, key, value.quantity, available[key]?.stock ?: 0
          )
        }
        .also { throw Exception("he order contains products with insufficient stock.") }

    }

    return available.mapValues { (id, product) ->
      val requestedQty = requested[id]?.quantity ?: 0
      product.copy(stock = product.stock - requestedQty)
    }
  }

  private fun getOrder(id: UUID) =
    orderRepository
      .findById(id)
      .getOrElse { throw Exception("Order $id not found.") }

}

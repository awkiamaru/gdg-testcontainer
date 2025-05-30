package com.awkiamaru

import com.awkiamaru.controllers.Request
import com.awkiamaru.entities.CustomerEntity
import com.awkiamaru.entities.OrderEntity
import com.awkiamaru.entities.OrderStatus
import com.awkiamaru.entities.ProductEntity
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.micronaut.http.HttpStatus
import io.micronaut.test.extensions.kotest5.annotation.MicronautTest
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

@MicronautTest(transactional = false)
class E2ETest(
  private val cleanup: DatabaseCleanUpService,
  private val spec: RequestSpecification) : FeatureSpec({

  afterTest { cleanup() }

  feature("Place Order") {
    scenario("Should successfully place an order when all products are in stock") {
      val productRequests = listOf(Fixtures.requestProduct(), Fixtures.requestProduct())
      val customerRequest = Fixtures.requestCustomer()


      val createdProducts = productRequests.map { createProduct(spec, it) }

      val customerResponse = createCustomer(spec, customerRequest)


      val orderRequest = Fixtures.requestOrder(
        customerId = customerResponse!!.id,
        items = createdProducts.map { Request.OrderItem(it!!.id, it.stock) }
      )

      val orderResponse = createOrder(spec, orderRequest)



      eventually((3).seconds) {
        val orderState = getOrderById(spec, orderResponse!!.id)

        orderState!!.status shouldBe OrderStatus.CONFIRMED

        createdProducts.forEach {
          val updatedProduct = getProductById(spec, it!!.id)

          updatedProduct!!.stock shouldBe 0

        }
      }
    }

    scenario("Should cancel order when requested quantity exceeds available stock") {
      val productRequests = listOf(Fixtures.requestProduct(), Fixtures.requestProduct())
      val customerRequest = Fixtures.requestCustomer()


      val createdProducts = productRequests.map { createProduct(spec, it) }

      val customerResponse = createCustomer(spec, customerRequest)


      val orderRequest = Fixtures.requestOrder(
        customerId = customerResponse!!.id,
        items = createdProducts.map { Request.OrderItem(it!!.id, it.stock + 1) }
      )

      val orderResponse = createOrder(spec, orderRequest)



      eventually((3).seconds) {
        val orderState = getOrderById(spec, orderResponse!!.id)

        orderState!!.status shouldBe OrderStatus.CANCELED

        createdProducts.forEach {
          val updatedProduct = getProductById(spec, it!!.id)

          updatedProduct!!.stock shouldBe it.stock

        }
      }
    }
  }
})

private fun getOrderById(spec: RequestSpecification, orderId: UUID): OrderEntity? =
  spec.given().contentType(ContentType.JSON)
    .get("/v1/orders/$orderId")
    .then()
    .statusCode(HttpStatus.OK.code)
    .extract()
    .`as`(OrderEntity::class.java)

private fun getProductById(spec: RequestSpecification, productId: UUID): ProductEntity? =
  spec.given().contentType(ContentType.JSON)
    .get("/v1/products/$productId")
    .then()
    .statusCode(HttpStatus.OK.code)
    .extract()
    .`as`(ProductEntity::class.java)

private fun createOrder(spec: RequestSpecification, orderRequest: Request.Order): OrderEntity? =
  spec
    .given()
    .contentType(ContentType.JSON)
    .body(orderRequest)
    .post("/v1/orders")
    .then()
    .statusCode(HttpStatus.CREATED.code)
    .extract()
    .`as`(OrderEntity::class.java)

private fun createCustomer(spec: RequestSpecification, customerRequest: Request.Customer): CustomerEntity? =
  spec.given()
    .contentType(ContentType.JSON)
    .body(customerRequest)
    .post("/v1/customers")
    .then()
    .statusCode(HttpStatus.CREATED.code)
    .extract()
    .`as`(CustomerEntity::class.java)

private fun createProduct(spec: RequestSpecification, it: Request.Product): ProductEntity? =
  spec.given()
    .contentType(ContentType.JSON)
    .body(it)
    .post("/v1/products")
    .then()
    .statusCode(HttpStatus.CREATED.code)
    .extract()
    .`as`(ProductEntity::class.java)
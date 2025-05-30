package com.awkiamaru.controllers

import com.awkiamaru.entities.ProductRepository
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Get
import java.util.UUID

@Controller("/products")
class ProductController(
  private val productRepository: ProductRepository
) {

  @Post
  fun create(@Body body: Request.Product) = body.toEntity().run(productRepository::save)

  @Get("/{productId}")
  fun findById(productId: UUID) = productRepository.findById(productId)
}
package com.awkiamaru

import io.kotest.core.spec.style.FeatureSpec
import io.micronaut.test.extensions.kotest5.annotation.MicronautTest

@MicronautTest(transactional = false)
class E2ETest(private val cleanup: DatabaseCleanUpService) : FeatureSpec({

  afterTest { cleanup() }

  feature("Place Order") {
    scenario("Should successfully place an order when all products are in stock") {

    }

    scenario("Should cancel order when requested quantity exceeds available stock") {

    }
  }
})
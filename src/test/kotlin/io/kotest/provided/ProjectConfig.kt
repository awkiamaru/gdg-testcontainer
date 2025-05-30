package io.kotest.provided

import com.awkiamaru.TestContainersEnvironment
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.BeforeProjectListener
import io.micronaut.test.extensions.kotest5.MicronautKotest5Extension
import java.util.TimeZone
import java.time.ZoneOffset.UTC


object TestContainerExtension : BeforeProjectListener, AfterProjectListener {
    override suspend fun beforeProject() {
        TimeZone.setDefault(TimeZone.getTimeZone(UTC))
        TestContainersEnvironment.start()
    }

    override suspend fun afterProject() {
        TestContainersEnvironment.stop()
    }
}


object ProjectConfig : AbstractProjectConfig() {
    override fun extensions() = listOf(MicronautKotest5Extension, TestContainerExtension)
}

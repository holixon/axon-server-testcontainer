package io.holixon.axon.testcontainer

import org.springframework.test.context.DynamicPropertyRegistry

object AxonServerContainerSpring {

  fun AxonServerContainer.addDynamicProperties(registry: DynamicPropertyRegistry) = with(registry) {
    add("axon.axonserver.servers") { url }
  }

}

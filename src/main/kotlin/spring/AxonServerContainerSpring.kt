// by using a jvmName, java can easily access static methods of extension functions.
@file:JvmName("AxonServerContainerSpring")

package io.holixon.axon.testcontainer.spring

import io.holixon.axon.testcontainer.AxonServerContainer
import org.springframework.test.context.DynamicPropertyRegistry

/**
 * Use this to configure a [org.springframework.test.context.DynamicPropertySource] for use of the containes with spring test/spring-starters.
 */
fun AxonServerContainer.addDynamicProperties(registry: DynamicPropertyRegistry) = with(registry) {
  add("axon.axonserver.servers") { url }
}

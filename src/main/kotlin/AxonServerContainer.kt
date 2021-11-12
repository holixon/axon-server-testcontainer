package io.holixon.axon.testcontainer

import mu.KLogging
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.Wait

class AxonServerContainer(dockerImageName: String = DEFAULT_DOCKER_IMAGE) : GenericContainer<AxonServerContainer>(dockerImageName){
  companion object : KLogging() {
    const val DOCKER_VENDOR = "axoniq"
    const val DOCKER_IMAGE = "axonserver"
    const val AXON_SERVER_VERSION = "4.5.8"

    const val DEFAULT_DOCKER_IMAGE = "$DOCKER_VENDOR/$DOCKER_IMAGE:$AXON_SERVER_VERSION"
  }

  init {
    withLogConsumer(Slf4jLogConsumer(logger))
    withExposedPorts(8024, 8124)
    withEnv("AXONIQ_AXONSERVER_DEVMODE_ENABLED", "true")
    waitingFor(Wait.forLogMessage(".*Started AxonServer.*\\n", 1))
  }

  val restUrl by lazy {
    "http://localhost:${getMappedPort(8024)}"
  }

  val url by lazy {
    "localhost:${getMappedPort(8124)}"
  }

}

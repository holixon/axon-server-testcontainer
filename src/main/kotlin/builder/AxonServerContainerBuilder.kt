package io.holixon.axon.testcontainer.builder

import io.holixon.axon.testcontainer.AxonServerContainer
import io.holixon.axon.testcontainer.AxonServerContainer.Companion.DockerImage
import io.holixon.axon.testcontainer.AxonServerContainer.Companion.EnvironmentVariables
import io.holixon.axon.testcontainer.AxonServerContainer.Companion.Ports
import org.slf4j.Logger
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.Wait

class AxonServerContainerBuilder {

  private val environment = mutableMapOf<String, String>()
  private var dockerImageName: String = DockerImage.DEFAULT_DOCKER_IMAGE
  private var logger: Logger = AxonServerContainer.logger

  fun enableDevMode() = apply {
    environment[EnvironmentVariables.DEVMODE] = "true"
  }


  fun build() = AxonServerContainer(
    dockerImageName = dockerImageName,
    initializer = {
      withLogConsumer(Slf4jLogConsumer(logger))
      withExposedPorts(Ports.DEFAULT_REST_PORT, Ports.DEFAULT_GRPC_PORT)
      environment.forEach { k, v ->
        withEnv(k, v)
      }
      waitingFor(Wait.forLogMessage(".*Started AxonServer.*\\n", 1))
    },
    environment = environment
  )
}

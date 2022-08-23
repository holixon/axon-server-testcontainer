package io.holixon.axon.testcontainer

import io.holixon.axon.testcontainer.builder.AxonServerContainerBuilder
import mu.KLogging
import org.testcontainers.containers.GenericContainer

/**
 * A testcontainer instance running an axon server image
 * in docker so it can be used during tests.
 */
class AxonServerContainer(
  /**
   * the docker image to start (defaults to `axoniq/axon-server:VERSION`).
   */
  dockerImageName: String,
  /**
   * The code to be excuted during the `init{}` phase.
   */
  initializer: AxonServerContainer.() -> Unit,

  /**
   * Map of env properties, only used to display in `toString()`.
   */
  val environment: Map<String, String>
) : GenericContainer<AxonServerContainer>(dockerImageName) {
  companion object : KLogging() {
    /**
     * The ports used by axon server.
     */
    object Ports {
      const val DEFAULT_REST_PORT = 8024
      const val DEFAULT_GRPC_PORT = 8124
    }

    /**
     * Default coordinates of the docker-hub image.
     */
    object DockerImage {
      const val DOCKER_VENDOR = "axoniq"
      const val DOCKER_IMAGE = "axonserver"
      const val AXON_SERVER_VERSION = "4.6.2-dev"
      const val DEFAULT_DOCKER_IMAGE = "$DOCKER_VENDOR/$DOCKER_IMAGE:$AXON_SERVER_VERSION"

      fun buildDockerImageName(version: String) = "$DOCKER_VENDOR/$DOCKER_IMAGE:$version"
    }

    /**
     * Keys of possible environment variables.
     */
    object EnvironmentVariables {
      const val DEVMODE = "AXONIQ_AXONSERVER_DEVMODE_ENABLED"
    }

    /**
     * Creates a builder instance to create a new container.
     */
    @JvmStatic
    fun builder() : AxonServerContainerBuilder = AxonServerContainerBuilder()
  }

  /**
   * Set up the docker container.
   */
  init {
    initializer.invoke(this)
  }

  /**
   * The URL of the local REST root path.
   */
  val restUrl by lazy {
    "http://localhost:${getMappedPort(Ports.DEFAULT_REST_PORT)}"
  }

  /**
   * The url of the `grpc` interface. This has to be used to configure the
   * gateways so they can communicate with the container.
   */
  val url by lazy {
    "localhost:${getMappedPort(Ports.DEFAULT_GRPC_PORT)}"
  }

  override fun toString() = "AxonServerContainer(" +
    "restUrl='$restUrl'" +
    ", url='$url'" +
    ", environment=$environment" +
    ")"

}

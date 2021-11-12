package io.holixon.axon.testcontainer

import io.holixon.axon.testcontainer.AxonServerContainerSpring.addDynamicProperties
import io.holixon.axon.testcontainer._itest.AxonServerContainerTestApplication
import io.holixon.axon.testcontainer._itest.BankAccountDto
import io.holixon.axon.testcontainer._itest.CreateBankAccountCommand
import io.holixon.axon.testcontainer._itest.FindBankAccountById
import mu.KLogging
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.messaging.responsetypes.ResponseType
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.*

@SpringBootTest(classes = [AxonServerContainerTestApplication::class], webEnvironment = WebEnvironment.NONE)
@Testcontainers
internal class AxonServerContainerITest {
  companion object : KLogging() {

    @Container
    val axon = AxonServerContainer()

    @JvmStatic
    @DynamicPropertySource
    fun axonProperties(registry: DynamicPropertyRegistry) = axon.addDynamicProperties(registry)
  }

  @Autowired
  private lateinit var commandGateway: CommandGateway

  @Autowired
  private lateinit var queryGateway: QueryGateway

  @Test
  internal fun `start axon server, run cmd, evt and query`() {
    val accountId = UUID.randomUUID().toString()

    commandGateway.sendAndWait<Any>(CreateBankAccountCommand(accountId = accountId, initialBalance = 100))

    val account = queryGateway.query(FindBankAccountById(accountId), ResponseTypes.optionalInstanceOf(BankAccountDto::class.java))
      .join().orElseThrow()

    assertThat(account.accountId).isEqualTo(accountId)
    assertThat(account.balance).isEqualTo(100)
  }
}

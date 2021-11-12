package io.holixon.axon.testcontainer

import io.holixon.axon.testcontainer.kotlin.KotlinTestApplication
import io.holixon.axon.testcontainer.spring.addDynamicProperties
import mu.KLogging
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.commandhandling.gateway.CommandGateway
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

@SpringBootTest(classes = [KotlinTestApplication::class], webEnvironment = WebEnvironment.NONE)
@Testcontainers
internal class AxonServerContainerKotlinITest {
  companion object : KLogging() {

    @Container
    val axon = AxonServerContainer.builder()
      .enableDevMode()
      .build().apply {
        logger.info { "------ $this" }
      }

    @JvmStatic
    @DynamicPropertySource
    fun axonProperties(registry: DynamicPropertyRegistry) = axon.addDynamicProperties(registry)
  }

  @Autowired
  private lateinit var commandGateway: CommandGateway

  @Autowired
  private lateinit var queryGateway: QueryGateway

  @Test
  fun `start axon server, run cmd, evt and query`() {
    val accountId = UUID.randomUUID().toString()

    commandGateway.sendAndWait<Any>(KotlinTestApplication.CreateBankAccountCommand(accountId = accountId, initialBalance = 100))

    val account = queryGateway.query(
      KotlinTestApplication.FindBankAccountById(accountId),
      ResponseTypes.optionalInstanceOf(KotlinTestApplication.BankAccountDto::class.java)
    ).join().orElseThrow()

    assertThat(account.accountId).isEqualTo(accountId)
    assertThat(account.balance).isEqualTo(100)
  }
}

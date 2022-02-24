# Axon Server TestContainer

Running axon server in [testcontainers](https://www.testcontainers.org/) tests.

[![Build Status](https://github.com/holixon/axon-server-testcontainer/workflows/Development%20branches/badge.svg)](https://github.com/holixon/axon-server-testcontainer/actions)
[![sponsored](https://img.shields.io/badge/sponsoredBy-Holisticon-RED.svg)](https://holisticon.de/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.holixon.axon.testing/axon-server-testcontainer/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.holixon.axon.testing/axon-server-testcontainer)


## Idea

Run an Axon Server docker container from within your (junit) tests.

## Usage

```
<dependency>
    <groupId>io.holixon.axon.testing</groupId>
    <artifactId>axon-server-testcontainer</artifactId>
    <version>0.0.1</version>
    <scope>test</scope>
</dependency>
```

see [AxonServerContainerITest](https://github.com/holixon/axon-server-testcontainer/blob/58194a1ebe71fff1d953debcda4d7b1dc37e8271/src/test/kotlin/AxonServerContainerKotlinITest.kt)


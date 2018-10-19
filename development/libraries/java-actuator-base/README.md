# Actuator `Java` Library 

Main dependency to use during the development of actuators developed in `Java`.


## Prerequisites

You need [maven](https://maven.apache.org/) to build this project.

If you cannot install `maven` in your system, you should use the `docker` image available at [libraries](../). In this case, `docker` is mandatory.


## Build

To build the library, you need to run the following command.

```sh
mvn clean install
```

## Usage

To use the library in the development of your actuator, you just need to include the library in your [maven](https://maven.apache.org/) project, using the code below.

```xml
<dependency>
    <groupId>eubr.atmosphere.tma.actuator</groupId>
    <artifactId>java-actuator-base</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

Note: check the [demo-actuator-java](../../actuators/demo-actuator-java) for mode detailed demonstration of the usage.

## Main Features

- **Actuator**: interface to be implemented by the Actuators with the needed operations;
- **ActuatorPayload**: class to be used as parameter of the `act` operation according to the definition in [here](https://github.com/eubr-atmosphere/tma-framework-e/tree/master#actuators-definition);
- **DecryptFilter**: filter used to both decrypt and encrypt the request from the `Executor` component to the Actuator. The path definition to the public and private key of the actuator as well as the public key from the executor should be provided in the configuration file, whose name should be `application.properties`. One example can be seen [here](https://github.com/eubr-atmosphere/tma-framework-e/blob/master/development/actuators/demo-actuator-java/src/main/resources/application.properties);
- **PropertiesManager**: class used to manipulate the configuration file.

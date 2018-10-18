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

FROM    joseadp/tma-utils

ENV     execute      /atmosphere/tma/execute

#       Adding Execute Component
WORKDIR ${execute}/tma-execute

#       Prepare by downloading dependencies
COPY    pom.xml     ${execute}/tma-execute/pom.xml

#       Adding source, compile and package into a fat jar
COPY    src ${execute}/tma-execute/src
RUN     ["mvn", "install"]

RUN     ["cp", "-r", "bin", "/atmosphere/tma/execute/bin"]

CMD ["java", "-jar", "/atmosphere/tma/execute/bin/tma-execute-0.0.1-SNAPSHOT.jar"]

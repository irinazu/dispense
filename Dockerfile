FROM openjdk:11
ADD target/dispense-0.0.1-SNAPSHOT.jar dispense.jar
ENTRYPOINT ["java","-jar","dispense.jar"]
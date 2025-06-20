FROM openjdk:11
VOLUME /tmp
EXPOSE 8090
ADD ./target/ms-mobile-wallet-0.0.1-SNAPSHOT.jar ms-mobile-wallet.jar
ENTRYPOINT ["java","-jar","/ms-mobile-wallet.jar"]
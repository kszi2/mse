FROM gradle:7-jdk-jammy AS mse_build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN ["gradle", "jar", "--no-daemon"]

FROM ibm-semeru-runtimes:open-17-jdk
RUN mkdir /app
COPY --from=mse_build /home/gradle/src/build/libs/*.jar /app/mse.jar
ENTRYPOINT ["java", "-jar", "/app/mse.jar"]
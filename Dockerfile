FROM gradle:7-jdk-jammy AS mse_build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN ["gradle", "distTar"]

FROM ibm-semeru-runtimes:open-17-jdk
RUN mkdir /app
COPY --from=mse_build /home/gradle/src/build/distributions/mse-*.tar /app/mse.tar
WORKDIR /app
RUN tar -xf mse.tar --strip-components=1
ENTRYPOINT ["bin/mse"]

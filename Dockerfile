#FROM alpine/curl
#RUN curl --location --fail https://repo1.maven.org/maven2/com/datadoghq/dd-java-agent/0.115.0/dd-java-agent-0.115.0.jar -o dd-java-agent.jar
#RUN curl --location --fail https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v1.19.1/opentelemetry-javaagent.jar -o opentelemetry-javaagent.jar
#RUN curl --location --fail https://repo1.maven.org/maven2/com/google/cloud/opentelemetry/exporter-auto/0.23.0-alpha/exporter-auto-0.23.0-alpha.jar -o exporter-auto.jar
#
#FROM gcr.io/distroless/java17-debian11
#
#COPY --from=0 dd-java-agent.jar /opt/
#COPY --from=0 opentelemetry-javaagent.jar /opt/
#COPY --from=0 exporter-auto.jar /opt/
##ADD build/libs/api.jar /opt/

FROM gcr.io/distroless/java17-debian11
ADD build/libs/api.jar /opt/
WORKDIR /opt
CMD ["api.jar"]
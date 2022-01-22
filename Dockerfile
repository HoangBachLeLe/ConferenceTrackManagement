FROM gradle:jdk17-alpine AS BUILD
WORKDIR /conference_track_management_build
COPY . /conference_track_management_build
RUN gradle bootJar

FROM eclipse-temurin:17-jre-alpine
WORKDIR /conference_track_management
COPY --from=BUILD /conference_track_management_build/build/libs/*.jar /conference_track_management/conference_track_management.jar
EXPOSE 8080
CMD java -jar conference_track_management.jar
./gradlew prepareDocker

cd build/docker

docker build -t rpalcolea/languages-api:v1.0 .

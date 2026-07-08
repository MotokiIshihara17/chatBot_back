# 1. まずはJavaが入った環境（ビルド用）を用意する
FROM gradle:8-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src

# 2. プログラムを実行できる形（JARファイル）に変換する
RUN ./gradlew bootJar --no-daemon

# 3. 本番用に、アプリを動かすためだけの軽量なJava環境を用意する
FROM eclipse-temurin:21-jre-jammy
EXPOSE 8082

# 4. 作成したプログラムをコピーして、起動する
COPY --from=build /home/gradle/src/build/libs/*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

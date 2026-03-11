# Estágio de build: compila o projeto usando a imagem oficial do Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copia as dependências e o código fonte
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
# Executa o build da aplicação ignorando os testes (mais rápido)
RUN mvn clean package -DskipTests

# Estágio de execução: imagem enxuta apenas com o JRE para o servidor rodar
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copia o JAR compilado do passo de build anterior
COPY --from=build /app/target/agnes-chatbot-1.0.0.jar app.jar

# A porta padrão do Spring
EXPOSE 8080

# Comando de inicialização
ENTRYPOINT ["java", "-jar", "app.jar"]

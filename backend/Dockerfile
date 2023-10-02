# Utilizar la imagen base que ya contiene Ubuntu, Maven y Java 18
FROM brunito/infra-special-ubuntu:1.0.0

# Crear la carpeta /app en el sistema de archivos del contenedor
RUN mkdir -p root/app

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /root/app

COPY /target/equipo4-0.0.1-SNAPSHOT.jar /root/app/


EXPOSE 8090


# Comando para ejecutar cualquier archivo JAR en el directorio target
CMD ["java", "-jar", "equipo4-0.0.1-SNAPSHOT.jar"]
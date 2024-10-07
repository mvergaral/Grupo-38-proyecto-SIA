# Proyecto SIA - Grupo 38

## Integrantes

- **20.053.858-7** - **Matías Vergara**
- **20.447.126-6** - **Ariel Villar**


## Requisitos del sistema

A continuación, se listan las versiones de software recomendadas para ejecutar este proyecto correctamente:

- **Java SDK**: JDK 21 o superior
- **PostgreSQL**: 14 o superior
- **Maven**: 3.8.8 o superior

### Recomendaciones para sistemas operativos

#### Windows
1. **Instalar JDK 21:**
   - Descarga e instala JDK 21 desde el [sitio oficial de Oracle](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html).
   - Verifica la instalación ejecutando en el terminal:
     ```bash
     java -version
     ```

2. **Instalar PostgreSQL:**
   - Descarga e instala PostgreSQL desde el [sitio oficial](https://www.postgresql.org/download/windows/).
   - Crea una base de datos y toma nota de las credenciales de acceso.

3. **Instalar Maven:**
   - Descarga e instala Maven desde [Apache Maven](https://maven.apache.org/download.cgi).
   - Verifica la instalación ejecutando en el terminal:
     ```bash
     mvn -version
     ```

4. **Clonar el repositorio y ejecutar el proyecto:**
   - Abre una terminal y clona el repositorio:
     ```bash
     git clone https://github.com/mvergaral/Grupo-38-proyecto-SIA.git
     cd grupo-38-proyecto-sia
     ```
   - Ejecuta el siguiente comando para compilar y ejecutar el proyecto:
     ```bash
     mvn clean javafx:run
     ```

#### Linux (Ubuntu/Debian)
1. **Instalar JDK 21:**
   - Instala JDK 21 mediante:
     ```bash
     sudo apt update
     sudo apt install openjdk-21-jdk
     java -version
     ```

2. **Instalar PostgreSQL:**
   - Instala PostgreSQL usando:
     ```bash
     sudo apt update
     sudo apt install postgresql postgresql-contrib
     ```

3. **Instalar Maven:**
   - Instala Maven con el siguiente comando:
     ```bash
     sudo apt install maven
     mvn -version
     ```

4. **Clonar el repositorio y ejecutar el proyecto:**
   - Abre una terminal y clona el repositorio:
     ```bash
     git clone <URL_DEL_REPOSITORIO>
     cd grupo-38-proyecto-sia
     ```
   - Ejecuta el proyecto con Maven:
     ```bash
     mvn clean javafx:run
     ```

#### macOS
1. **Instalar JDK 21:**
   - Usa `Homebrew` para instalar JDK 21:
     ```bash
     brew install openjdk@21
     export PATH="/usr/local/opt/openjdk@21/bin:$PATH"
     java -version
     ```

2. **Instalar PostgreSQL:**
   - Usa `Homebrew` para instalar PostgreSQL:
     ```bash
     brew install postgresql
     brew services start postgresql
     ```

3. **Instalar Maven:**
   - Usa `Homebrew` para instalar Maven:
     ```bash
     brew install maven
     mvn -version
     ```

4. **Clonar el repositorio y ejecutar el proyecto:**
   - Abre una terminal y clona el repositorio:
     ```bash
     git clone <URL_DEL_REPOSITORIO>
     cd grupo-38-proyecto-sia
     ```
   - Ejecuta el siguiente comando para compilar y ejecutar el proyecto:
     ```bash
     mvn clean javafx:run
     ```

---

### Base de datos PostgreSQL

El proyecto utiliza una base de datos PostgreSQL. En caso de tener credenciales distintas a las predeterminadas, se deben modificar las credenciales en el archivo `src/main/java/com/grupo38/config/DatabaseInitializer.java`. Se debe modificar la siguiente sección:

```java
String user = "User";  // El superusuario de PostgreSQL
String password = "Password";
```

En ambos metodos del DatabaseInitializer que ocupan estas variables. En caso contrario la creación automática de la base de datos utilizara postgres como usuario.


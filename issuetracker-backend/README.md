When starting the project, you have to pass a private key that will be used for signing the JWT tokens.
The key should be generated using <a href="https://www.devglan.com/online-tools/hmac-sha256-online">HMAC-SHA256</a>.
Also, you have to pass data, related to DB connection. All of this is passed as an env variable, called
*SPRING_APPLICATION_JSON*, containing a JSON
object that has the following form:

```json
{
  "services": {
    "db": {
      "connection_url": "...",
      "port": ...,
      "driver_class_name": "...",
      "database_name": "...",
      "username": "...",
      "password": "..."
    },
    "auth": {
      "jwt_private_key": "..."
    },
    "app": {
      "system_admin": {
        "first_name": "...",
        "last_name": "...",
        "username": "...",
        "password": "..."
      }
    }
  }
}
```

Running in Maven:

```shell
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-DSPRING_APPLICATION_JSON=<your_object_here>"
```

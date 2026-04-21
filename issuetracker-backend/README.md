When starting the project, you have to pass a private key that will be used for signing the JWT tokens.
The key should be passed as an environment variable, called JWT_KEY.
The key should also be generated using <a href="https://www.devglan.com/online-tools/hmac-sha256-online">HMAC-SHA256</a>.

Running in Maven:
```shell
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-DJWT_KEY=<your_key_here>"
```

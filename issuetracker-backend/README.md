When starting the project, you have to pass a private key that will be used for signing the JWT tokens.
The key should be generated using <a href="https://www.devglan.com/online-tools/hmac-sha256-online">HMAC-SHA256</a>.
</br> </br> Also, you have to pass a JSON of the following format: [example](./example-spring-application-json.json)

## Skip forgot password email sending

If you want to skip forgot password email sending, send a POST request to /featureflags with the following body:

```json
{
  "name": "SKIP_EMAIL",
  "value": true
}
```

*Note that you have to be logged in as an admin in order to execute this request!*

## Generate Javadoc

In order to generate javadoc, you can:

```shell
mvn javadoc:javadoc
```

Alternatively, if you don't have maven installed:

- on Mac:

```shell
./mvnw javadoc:javadoc
```

- on Windows:

```shell
./mvnw.cmd javadoc:javadoc
```

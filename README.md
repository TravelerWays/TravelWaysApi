# Travel way

## Mail

If you want to use mail system, you have fill password in ```travel.ways.mail.password``` and change
value ```travel.ways.mail.send``` to `true`.

## Documentation

You can check API documentation on `http://localhost:8080/swagger-ui` (if it doesn't work then add 
`/#` at the end of the link). To authorize go to `default` section and click Try it out on login endpoint,
then copy JWT from response and paste `Bearer <JWT_TOKEN>` in `Authorize` at the top of the page.
If you want to disable swagger then in `application.properties` set `springfox.documentation.enabled=false`.
# Challenge - Writing an External Integration

Integrating with external partners via APIs is the rule nowadays. Commonly they are modern and well design... but 
sometimes they are not!

Your task is to write a layer on top of a (fictional) poorly designed API, providing a better experience to consume the 
resource. It's expected:

* a client that consumes two endpoints of the old api
* a webserver, exposing only one endpoint: `POST /account`

This document outlines the input your program will receive and the expected output. It also
includes a little background information on the problem domain.

## Creating an account

The external API is not available yet. No worries, we have the documentation!

To create a new account, the external API exposes `POST http://api.partner.com/request-account` and expects data in the following format (this is a valid input):

```json
{
  "name": "John Lennon",
  "email": "john.lennon@jmail.com",
  "date_of_birth": "1980-12-08",
  "country" : {
    "code": "UK"
  }
}
```
The [country code is an alpha3](https://www.iban.com/country-codes).

Note that there are no validations on their side. If the payload is somehow different from the example above, their api 
can behave in unexpected ways.

If it's a successful request, it will respond with 200:
```json
{
  "status": 0,
  "request_id": "{uuid}",
  "accepted_at": "2021-11-05T11:03:42Z"
}
```

However, if something goes wrong, it still responds with 200, but the body is:
```json
{
  "status": -1, 
  "error_message": "Something is wrong"
}
```

In this case, you should retry the request once before assuming it's a failure.

## Checking if the account is ready

Unfortunately, the external partner has never heard of webhooks. They expose
`GET http://api.partner.com/status/{request_id}` for us to check the account status. The possible responses are:

```json
{
  "request_id": "{request_id}",
  "status": {
    "code": 1,
    "description": "Processing"
  }
}
```

```json
{
  "request_id": "{request_id}",
  "status": {
    "code": 2,
    "description": "Ready"
  }
}
```

```json
{
  "request_id": "{request_id}",
  "status": {
    "code": 3,
    "description": "Cancelled"
  }
}
```

They can take up to 10 seconds to verify an account and respond with `Ready` or `Cancelled`. Your task here is:

* to check the account status, looking for `Ready` or `Cancelled`
* to check the status once every second, up to 4 seconds

If the account is ready or cancelled, the code stops checking. After the fourth attempt, if the account is not complete,
the code stops checking as well. In this case, the status of the account should be considered `In Progress`.

## The output

A successful request to your api should return one of the following three responses:

```json
{ "status": "Ready" }
```

```json
{ "status": "Cancelled" }
```

```json
{ "status": "In Progress" }
```

## Your solution

### Submitting

Your code should be submitted in a tarball that contains a `./bin/server` executable script.

This command will be expected to speed up the webserver and expose the http://localhost:8080/account endpoint.

Your program will be expected to run on OSX or Linux. If your program requires special pre-requisites
(if it needs the Go compiler for example) please include it in your submission's README.

### Tips

The problem is not specific about some requirements on purpose, we want to see how you will deal with them. 

Please write your solution in a language you feel confident in. Your program should both produce
the expected output and be well written. Java is allowed; Spring is not.

### ***** Solution applied ******

My solution was to create a retry system where I can control de timeouts in communication between services. The system can be configured with parameters.

- Jersey
- JAX-RS
- Lombok

0 - it is necessary to have maven on the machine 

1 - extract to any folder

2 - it will create the application folder 

3 - cd application 

4 - mvn clean compile 

5 - mvn exec:java 

6 - testing in postman: 
 
 - http://localhost:8080/account 
 - POST
 - body :

```json
{
  "name": "John Lennon",
  "email": "john.lennon@jmail.com",
  "date_of_birth": "1980-12-08",
  "country" : {
    "code": "UK"
  }
}
```


ManyWho Swagger Service
=======================

This service allows you to connect ManyWho with an API with swagger description.

**This service is currently in development, and not yet recommended for use in production environments.**

## Configuration

### Actions

Actions are supported for not nested types.
E.g. type user
{
    "username": "jose",
    "id": "12345"
}

### Save, Load and Create

You will need to provide two configuration value, and we will Explain these values using the example [swagger petstore project](http://petstore.swagger.io)

 - **Swagger Description Url**

This is the url of your api description, in our case is http://petstore.swagger.io/v2/swagger.json

 - **Swagger ManyWho Mapper**

We define here how ManyWho is going to load, crate and update the Types (we will just deffine "User") using the petstore API.

````
[{
    "manyWhoType": "User",
    "externalId": "username",
    "load": {
        "verb": "get",
        "url": "http://petstore.swagger.io/v2/user/{paramUsername}",
        "parameters": [{
            "type": "query",
            "paramName": "paramUsername",
            "name": "id",
            "passedBy": "filter"
        }]
    },
    "create": {
        "verb": "post",
        "url": "http://petstore.swagger.io/v2/user",
        "parameters": []
    },
    "update": {
        "verb": "put",
        "url": "http://petstore.swagger.io/v2/user/{paramUsername}",
        "parameters": [{
            "type": "query",
            "paramName": "paramUsername",
            "name": "username",
            "passedBy": "object"
        }]
    }
}]
````

#### Lets take a look to this configuration example:

Each definition is an "manyWhoType", and we are defining how ManyWho can load, save or create an object of this Type. 

For the **load** we will call to "http://petstore.swagger.io/v2/user/{paramUsername}" with the verb "get", and the parameter "paramUsername" will be passed using the filter, and in the filter the name of this parameter is "id".

In the ManyWho tooling we can work like always, grab a load map, edit filter and select "Get the data based on a unique identifier", then assign or create a value for identifier.

The case of **update** it is similar, but in this case the verb is "put", and the "paramUsername" can be get from the object in the property "username"

Take a look now to the externalId, in this case is clear that the username is the value that unique identify the type "User". 
(The paramUsername is just a param, but in the update we can see how this parameter is realy the property username of the User Type)

## Usage

If you need to run your own instance of the service (e.g. for compliance reasons), it's easy to spin up following these
instructions:

#### Building

To build the service, you will need to have Maven 3 and a Java 8 implementation installed.

You can build the runnable shaded JAR:

```bash
$ mvn clean package
```

#### Running

The service is a Jersey JAX-RS application, that by default is run under the Grizzly2 server on port 8080 (if you use 
the packaged JAR).

##### Defaults

Running the following command will start the service listening on `0.0.0.0:8080/api/swagger/1`:

```bash
$ java -jar target/swagger-1.0-SNAPSHOT.jar
```

##### Custom Port

You can specify a custom port to run the service on by passing the `server.port` property when running the JAR. The
following command will start the service listening on port 9090 (`0.0.0.0:9090/api/swagger/1`):

```bash
$ java -Dserver.port=9090 -jar target/swagger-1.0-SNAPSHOT.jar
```

## Contributing

Contribution are welcome to the project - whether they are feature requests, improvements or bug fixes! Refer to 
[CONTRIBUTING.md](CONTRIBUTING.md) for our contribution requirements.

## License

This service is released under the [MIT License](http://opensource.org/licenses/mit-license.php).

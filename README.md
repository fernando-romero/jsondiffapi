# JsonDiffApi

Http json diff api developed with Spray, Akka and ReactiveMongo.

## Requirements to run

1. Sbt installed.
2. MongoDB running. By default, the app connects to a mongo instance running on `localhost`, a different host can be provided setting the `MONGO_HOST` environment variable, also you can set `MONGO_HOST_TEST` environment variable to use another host for integration tests, otherwise it will use `localhost`.

## Run unit tests

No database running needed. It tests internal behaviour and uses an in-memory storage.

```
sbt test
```

## Run integration tests

MongoDB instance must be running either at `localhost` or at `MONGO_HOST_TEST` if the environment variable was provided.

```
sbt it:test
```

## Run application

```
sbt run
```

Application will be available at [http://localhost:8080](http://localhost:8080).

## Scaladoc

Generate docs using:

```
sbt doc
```

## What it does?

*JsonDiffApi* compares two json objects and calcutates an *Insight*, which is an object containing the diffs, their offset and length. The rules it follows for the calculation are:

1. If both json objects are equal then the insight should state that the objects are equal and are of the same size, no diffs need to be calculated.

	```
	Example:

	Left: 	{"foo":"bar"}
	Right:	{"foo":"bar"}

	Insight returned by api:
	{
		"areEqual": true,
		"areEqualSize": true,
		"diffs": []
	}
```

2. If they are of different sizes then the insight should state that the objects are not equal and are not of the same size, no diffs need to be calculated.

	```
	Example:

	Left: 	{"foo":"bar"}
	Right:	{"foo":"bartolomeo"}

	Insight returned by api:
	{
		"areEqual": false,
		"areEqualSize": false,
		"diffs": []
	}
	```

3. If they are of the same size but not equal then the insight should state that the objects are not equal but they have the same size, diffs should show the offsets and lengths.

	```
	Example:

	Left: 	{"foo":"bar"}
	Right:	{"fee":"bur"}

	Insight returned by api:
	{
		"areEqual": false,
		"areEqualSize": true,
		"diffs": [
			{
				"offset": 3,
				"lenght": 2
			},
			{
				"offset": 9,
				"length": 1
			}
		]
	}
	```

We can see the diffs located at the positions 3 and 9.

## Endpoints

Left and Right objects can be saved with an unique id so the insight can be retrieved at a third endpoint. The left and right endpoints accept only json data (an object or an array) encoded with base64. Only requests with a `Content-Type: application/base64` header will be accepted. Also, after decoding the data must be a valid json, this json will be compacted if needed and saved to database.

Http methods are not checked by the api, only paths and data. So as long as the path, headers a payload are correct you can use any http method you want. Tests in the code use POST for left and right endpoints and GET for the insight endpoint.

The endpoints are the following:

### /v1/diff/:id/left

Responses:

| Code                         | Meaning																																				|
|------------------------------|--------------------------------------------------------------------------------|
| 204 (No content)             | The json was succesfully saved. If already exists for the id, it overrides it. |
| 415 (Unsupported media type) | A Content-Type different from `application/base64` was provided.               |
| 400 (Bad request)            | The data was base64 enconded but is not a valid json.                          |

For unix/linux users you can chain echo and openssl base64 encoding to provide the data to be sent using curl like this:

```
echo '{"foo":"bar"}' | openssl base64 | curl -d @- localhost:8080/v1/diff/test/left -H 'Content-Type: application/base64'
```

### /v1/diff/:id/right

Responses:

| Code                         | Meaning																																				|
|------------------------------|--------------------------------------------------------------------------------|
| 204 (No content)             | The json was succesfully saved. If already exists for the id, it overrides it. |
| 415 (Unsupported media type) | A Content-Type different from `application/base64` was provided.               |
| 400 (Bad request)            | The data was base64 enconded but is not a valid json.                          |

Curl example:

```
echo '{"foo":"bar"}' | openssl base64 | curl -d @- localhost:8080/v1/diff/test/right -H 'Content-Type: application/base64'
```

### /v1/diff/:id

| Code            | Meaning																																				           |
|-----------------|------------------------------------------------------------------------------------------|
| 200 (OK)        | An insight object is returned in json format.                                            |
| 404 (Not found) | Left and right data should be present in db for the given id, otherwise 404 is returned. |

Curl example:

```
curl localhost:8080/v1/diff/test -H 'Accept: application/json'
```

## Notes

* The data saved in the database is always the compact representation of a json. Therefore `{"foo":"bar"}` is equal to `{ "foo" : "bar" }`.
* The order of the fields is not considered or modified. Therefore `{"foo":"bar","mee":"moo"}` is not equal to `{"mee":"moo","foo":"bar"}`.
* [ReactiveMongo](http://reactivemongo.org/) is used instead of the official [Casbah](http://mongodb.github.io/casbah/) since the first is an akka based asynchronous, non-blocking solution. ~~Because actors should never block!~~
* [spray-template](https://github.com/spray/spray-template) was used as a template for this repo.

## License
MIT
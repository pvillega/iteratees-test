Iteratees Test
==============

This project is a test of Iteratees and real-time streaming on an ETL-like scenario.

The aim is to tinker with Iteratees, some testing tools and MongoDB. Don't expect great code, it's an experiment on areas I'm not (yet) an expert :)

The project will simulate receiving data from one source, process the messages and then write the output in a MongoDB database

## Requirements

To test this project you will need:

* Play 2.0.4
* MongoDB (optional, connection can be disabled)
* Redis (optional, connection can be disabled)
* A browser that supports Server Side Events [(SEE)](http://dev.w3.org/html5/eventsource/) like Chrome (only if you enable the web interface)

## How to start

Follow these steps to deploy the application:

* Clone this project
* (optionally) Set up a [MongoDB](http://www.mongodb.org/) installation and set the DB details in the `application.conf` file
* (optionally) Set up a [Redis](http://redis.io/) installation and set the DB details in the `application.conf` file
* Download and install [Play 2.0.4](http://www.playframework.org/download)
* In the folder of this project run `play start`  (press Control+D to kill the server)
* If the web interface is available (see configuration below) you can access it via http://localhost:9000/

## Configuration

The following options change the behaviour of the application:

* **enable.webInterface**: if true, the web interface that displays the stream traffic will be enabled. Otherwise it will return a 404 error.
* **fakeStream.enable**: if true an actor will generate fake requests every 500ms
* **fakeStream.server**: the location of the server, used to send the messages via POST when using the fake actor
* **mongo.stream.enable**: stores messages in Mongo DB (see config for MongoDB connection details)

## To test

You can use AB to test the streams:

    ab -c 100 -n 1000000 -p post -T application/json http://localhost:9000/receive

Create a file *post* with the content of the POST request

## Why SSE instead of WebSocket?

There are 2 reason for choosing SEE over WebSockets in this test:

* SSE is managed by the browser. That means that the browser takes care of reconnection, etc
* SSE is unidirectional server-browser. For apps like this sample you don't need the 2-way channel of WebSocket

Also, I already tested WebSockets, so it was less appealing :)

## Tools used

Besides Play 2.0.4, this project uses:

* [Bootstrap](http://twitter.github.com/bootstrap/) for layout
* [Modernizr](http://modernizr.com/)
* [Salat](https://github.com/leon/play-salat) to connect to MongoDB
* [Play Redis](https://github.com/typesafehub/play-plugins/tree/master/redis) to connect to Redis
* [JaCoCo](https://bitbucket.org/jmhofer/jacoco4sbt/wiki/Home) to check code coverage
* [Akka-Testkit](http://doc.akka.io/docs/akka/snapshot/scala/testing.html) to test Akka actors


## License

Copyright (c) 2012 Pere Villega

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.



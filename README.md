Iteratees Test
==============

This project is a test of Iteratees and real-time streaming on an ETL-like scenario.

The aim is to tinker with Iteratees, some testing tools and MongoDB. Don't expect great code, it's an experiment on areas I'm not (yet) an expert :)

The project will simulate receiving data from one source, process the messages and then write the output in a MongoDB database

## Requirements

To test this project you will need:

* Play 2.0.4
* MongoDB
* A browser that supports Websockets, like Chrome


## How to start

Follow these steps to deploy the application:

* Clone this project
* Set up a [MongoDB](http://www.mongodb.org/) installation and set the DB details in the `application.conf` file
* Download and install [Play 2.0.4](http://www.playframework.org/download)
* In the folder of this project run `play start`  (press Control+D to kill the server)
* If the web interface is available (see configuration below) you can access it via http://localhost:9000/


## Configuration

The following options change the behaviour of the application:

* **enable.webInterface**: if true, the web interface that displays the stream traffic will be enabled. Otherwise it will return a 404 error.


## License

Copyright (c) 2012 Pere Villega

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.



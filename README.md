# PacketStack
PacketStack is an asynchronous network level based framework to rapidly exchange data between a server and it's clients, built on top of [Netty](https://github.com/netty/netty).

### Features
- Completely asynchronous
- Store and request data
- Rapid data caching

### PacketStack as a data bridge
PacketStack has a built-in database service which is used by default, but it can also be used as a data bridge between MongoDB and SQL databases and the PacketStack service.
Databases are untouched until an operator forcefully writes to them or when the service closes. Due to PacketStack's asynchronous
nature, writing to the database does not lock the application and data exchanging still occurs at a rapid pace.
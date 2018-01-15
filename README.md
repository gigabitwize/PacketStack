# PacketStack
PacketStack is an asynchronous network level based framework to rapidly exchange data between a server and it's clients, built on top of [Netty](https://github.com/netty/netty).

### Features
- Completely asynchronous.
- Rapid read/write/flush data operations.
- Rapid data caching.
- Scalable grid of pipelines.
- Packet keep-alive management.
  - If the corresponding ending of a pipeline is unavailable, the packet gets cached.
- Complex objects support.

### Scalable pipeline grid
PacketStack is a scalable grid of pipelines each with an ending which handles read/write/flush operations.
Every connected client is forged into the grid of pipelines, because of this, packets will always find the end of a pipeline connected to the client.
If there is no available ending of a pipeline(a "looping pipeline") then the packet will get cached until the end of the pipeline is available, by default a packet with no ending
waits 3 hours before it disassembles itself.


### Rapid data caching
PacketStack caches data on the end of each pipeline, when the end of a pipeline breaks("leaking") the pipeline(including the data) gets moved over to another ending.
If there are no pipeline endings, the data gets cached on the PacketStack service and gets spread over the grid of pipelines once there are visible endings.
Data is always alive and thus always reachable until flushed, making read/write operations rapid. PacketStack splits heavy write operations in pieces
and handles them on multiple pipeline endings to ensure quality performance.

### PacketStack as a data bridge
PacketStack has a built-in database service which is used by default, but it can also be used as a data bridge between MongoDB and SQL databases and the PacketStack service.
Databases are untouched until an operator forcefully writes to them or when the service closes. Due to PacketStack's asynchronous and grid-styled
nature, writing to the database does not lock the application and data exchanging still occurs at a rapid pace.

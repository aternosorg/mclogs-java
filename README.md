# mclogs-java
A java library used to list and share log files to the [mclo.gs](https://mclo.gs) API.

### Features
- Remove IPv4 and IPv6 addresses before uploading the log
- Trim logs and shorten them to 10 MB before uploading

### Usage
This project is available from Maven Central, so you just need to add the dependency to your project:

#### Gradle
```gradle
dependencies {
    implementation 'gs.mclo:api:4.0.2'
}
```

#### Maven
```xml
<dependency>
  <groupId>gs.mclo</groupId>
  <artifactId>api</artifactId>
  <version>4.0.2</version>
</dependency>
```

### Upload a log file

Obtaining a log file:
```java
// by path
var log = new Log(Paths.get("./logs/latest.log"));
// or by raw content
log = new Log("example content");
```

Creating a client:
```java
// Create a client with a project name and version
var client = new MclogsClient("mclogs-java-example", "1.0.0");
// optionally with a minecraft version
client = new MclogsClient("mclogs-java-example", "1.0.0", "1.12.2");
// or with a custom user agent
client = new MclogsClient("mclogs-java-example/1.0.0");
```

Sharing the log file:
```java
// share the log file
CompletableFuture<UploadLogResponse> future = client.uploadLog(log);
UploadLogResponse response = future.get();
System.out.println(response.getUrl());
```

There are also shortcuts for posting raw content or a path:
```java
// share a log file by path
CompletableFuture<UploadLogResponse> future = client.uploadLog(Paths.get("./logs/latest.log"));
// share a log file by raw content
future = client.uploadLog("example content");
```

### Fetch a log file's contents
```java
String secondResponse = client.getRawLogContent("HpAwPry").get();
```

### Using a self-hosted instance of mclogs
```java
Instance instance = new Instance();
instance.setApiBaseUrl("https://api.logs.example.com");
instance.setViewLogUrl("https://api.logs.example.com");
client.setInstance(instance);
```

This library is used in the mclogs plugin and mods:
[Bukkit](https://github.com/aternosorg/mclogs-bukkit) |
[Forge](https://github.com/aternosorg/mclogs-forge) |
[Fabric](https://github.com/aternosorg/mclogs-fabric)

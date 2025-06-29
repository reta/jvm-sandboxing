# Sandboxing Java applications with GraalVM and GraalVM Espresso

An example project that demonstrates GraalVM polyglot capabilities and GraalVM Espresson engine to sandbox Java applications.

## Prerequisites

- Linux
- JDK-17 installed (assuming default location `/usr/lib/jvm/java-17-openjdk-amd64/`)
- JDK-24 installed (assuming default location `/usr/lib/jvm/java-24-openjdk-amd64/`)

## Build

- Build `app`: 

	```
	$ mvn install -f app/`
	```

- Run `sandbox-custom-jvm`:

	```
	$ export JAVA_HOME=/usr/lib/jvm/java-24-openjdk-amd64/
	$ mvn package exec:java -Dexec.mainClass="com.example.graalvm.sandbox.SandboxRunner" -Dexec.args="/usr/lib/jvm/java-17-openjdk-amd64/ app/target/app-0.0.1-SNAPSHOT.jar" -f sandbox-custom-jvm/
	```

- Run `sandbox-bundled-jvm`:

	```
	$ export JAVA_HOME=/usr/lib/jvm/java-24-openjdk-amd64/
	$ mvn package exec:java -Dexec.mainClass="com.example.graalvm.sandbox.SandboxRunner" -Dexec.args="app/target/app-0.0.1-SNAPSHOT.jar" -f sandbox-bundled-jvm/
	```
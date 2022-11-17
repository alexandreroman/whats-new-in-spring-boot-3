# Spring Boot 3.0 with Java 17

This module showcases the use of Java 17 for a Spring Boot 3.0 app.

## How to run this app?

Run this command to run the app:

```shell
./mvnw sprint-boot:run
```

The app is available at http://localhost:8080.

## Deploy this app to Kubernetes

Go to the [`k8s`](../k8s) directory and follow instructions to
start a local cluster on your workstation.

As soon as you have a Kubernetes cluster up and running, proceed to the next step.

From the [`k8s/wnisb3-java17`](../k8s/wnisb3-java17) directory, run this command to
deploy the app to your Kubernetes cluster:

```shell
make install
```

Run this command to uninstall the app from your cluster:

```shell
make uninstall
```

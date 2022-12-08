## What's new in SpringBoot 3 and Aria Apps

This repository contains configuration files to use Aria Apps as an Observability Plateform instead of the Grafana Stack

### Need a free trial Account?

[Start your VMware Aria Operations for Applications free 30-day trial now!](https://tanzu.vmware.com/observability-trial)

### Install the Aria Apps Operator

See Wavefront Operator [README.md](https://github.com/wavefrontHQ/wavefront-operator-for-kubernetes/blob/main/README.md)
```sh
kubectl apply -f https://raw.githubusercontent.com/wavefrontHQ/wavefront-operator-for-kubernetes/v2.0.3/deploy/kubernetes/wavefront-operator.yaml
or
kindops install wavefront-operator
```

[Generate an API Token](https://docs.wavefront.com/users_account_managing.html#generate-an-api-token)
```sh
kubectl create -n observability-system secret generic wavefront-secret --from-literal token=YOUR_WAVEFRONT_TOKEN
```

### Wavefront configuration

Edit [aria.yaml](aria.yaml) file to add:
* clusterName (string)
* wavefrontUrl (ex: https://longboard.wavefront.com/)

This template is a combination of the official scenari:
* [Enable Zipkin tracing](https://github.com/wavefrontHQ/wavefront-operator-for-kubernetes/blob/main/deploy/kubernetes/scenarios/wavefront-proxy-tracing.yaml)
* [Enable OTLP](https://github.com/wavefrontHQ/wavefront-operator-for-kubernetes/blob/main/deploy/kubernetes/scenarios/wavefront-proxy-oltp.yaml)

### Deploy app

```sh
make install
```

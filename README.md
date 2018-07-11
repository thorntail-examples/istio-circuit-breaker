# Istio Circuit Breaker Mission for Thorntail

## Purpose

Showcase Circuit Breaking in Istio in Thorntail applications

## Prerequisites

* Docker installed and running
* OpenShift and Istio environment up and running (See https://github.com/openshift-istio/istio-docs/blob/master/user-journey.adoc for details)

## Launcher Flow Setup

If the Booster is installed through the Launcher and the Continuous Delivery flow, no additional steps are necessary.

Skip to the _Use Cases_ section.

## Local Source to Image Build (S2I)

### Prepare the Namespace

Create a new namespace/project:
```
oc new-project <whatever valid project name you want>
```

### Build and Deploy the Application

#### With Source to Image build (S2I)

Run the following commands to apply and execute the OpenShift templates that will configure and deploy the applications:
```bash
find . | grep openshiftio | grep application | xargs -n 1 oc apply -f

oc new-app --template=thorntail-istio-circuit-breaker-greeting-service -p SOURCE_REPOSITORY_URL=https://github.com/wildfly-swarm-openshiftio-boosters/wfswarm-istio-circuit-breaker -p SOURCE_REPOSITORY_REF=master -p SOURCE_REPOSITORY_DIR=greeting-service
oc new-app --template=thorntail-istio-circuit-breaker-name-service -p SOURCE_REPOSITORY_URL=https://github.com/wildfly-swarm-openshiftio-boosters/wfswarm-istio-circuit-breaker -p SOURCE_REPOSITORY_REF=master -p SOURCE_REPOSITORY_DIR=name-service
```

## Use Cases

### Without Istio Configuration

1. Retrieve the URL for the Istio Ingress Gateway route, with the below command, and open it in a web browser.
    ```
    echo http://$(oc get route istio-ingressgateway -o jsonpath='{.spec.host}{"\n"}' -n istio-system)/thorntail-istio-circuit-breaker/
    ```
2. The user will be presented with the web page of the Booster
3. Click "Start" to issue repeating concurrent requests in batches of 10 to the greeting service
4. Click "Stop" to cease issuing more requests
5. The number of concurrent requests can be set to anything between 1 and 20
6. There should be no failures and all calls are ok

### With Istio Configuration

#### Initial Setup

1. Run `oc project <project>` to connect to the project created by the Launcher, or one you created in a previous step
2. Create a `VirtualService` for the name service, which is required to use `DestinationRule` later
    ````
    oc create -f rules/virtual-service.yml -n $(oc project -q)
    ````
3. Trying the application again you will notice no change from the current behavior

#### Istio Circuit Breaker Configuration

1. Apply a `DestinationRule` that activates Istio's Circuit Breaker on the name service,
configuring it to allow a maximum of 100 concurrent connections
    ````
    oc create -f rules/initial-destination-rule.yml -n $(oc project -q)
    ````
2. Trying the application again you should see no change,
as we're only able to make up to 20 concurrent connections which is not enough to trigger the circuit breaker.
3. Remove the initial destination rule
    ````
    oc delete -f rules/initial-destination-rule.yml
    ````
4. Apply a more restrictive destination rule
    ````
    oc create -f rules/restrictive-destination-rule.yml -n $(oc project -q)
    ````
5. Trying the application again you can see about a third of all requests are triggering the fallback response because the circuit is open
6. If we check "Simulate load", which adds a delay into how quickly the name service responds, and click "Start".
We now see that the majority of our calls trigger the fallback as our name service takes too long to respond.

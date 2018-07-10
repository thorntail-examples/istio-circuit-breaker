/*
 *  Copyright 2018 Red Hat, Inc, and individual contributors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package io.openshift.booster;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;

/**
 * @author Ken Finnigan
 */
@Path("/")
public class GreetingResource {

    private static final String NAME_SERVICE_URL = "http://thorntail-istio-circuit-breaker-name:8080";

    @GET
    @Path("/greeting")
    @Produces("application/json")
    public Response greeting(@QueryParam("from") String from, @QueryParam("delay") String delay) {
        try {
            Client client = ClientBuilder.newBuilder().build();
            WebTarget webTarget = client.target(NAME_SERVICE_URL);
            Invocation.Builder requestBuilder = webTarget.path("/api/name")
                    .queryParam("from", from)
                    .queryParam("delay", delay != null ? delay : "")
                    .request();

            Response nameResponse = requestBuilder.get();
            String name;

            if (HttpStatus.SC_SERVICE_UNAVAILABLE == nameResponse.getStatus()) {
                name = "Fallback";
            } else {
                name = nameResponse.readEntity(String.class);
            }

            return Response.ok()
                    .entity(new Greeting(String.format("Hello %s", name), from))
                    .build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity("Failed to communicate with `thorntail-istio-circuit-breaker-name` due to: " + e.getMessage())
                    .build();
        }
    }

    static class Greeting {
        private final String content;
        private final String from;

        public Greeting(String content, String from) {
            this.content = content;
            this.from = from;
        }

        public String getContent() {
            return content;
        }

        public String getFrom() {
            return from;
        }
    }

}

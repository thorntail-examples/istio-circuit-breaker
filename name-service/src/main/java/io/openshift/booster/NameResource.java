/*
 *
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
import javax.ws.rs.core.MediaType;

/**
 * @author Ken Finnigan
 */
@Path("/")
public class NameResource {
    private static final String DEFAULT_NAME = "World";

    @GET
    @Path("/name")
    @Produces(MediaType.TEXT_PLAIN)
    public String getName(@QueryParam("from") String from, @QueryParam("delay") String delay) throws InterruptedException {
        final String fromSuffix = from != null ? " from " + from : "";

        final String name = DEFAULT_NAME + fromSuffix;

        if (delay != null && !delay.isEmpty()) {
            int processingDelay;
            try {
                processingDelay = Integer.parseInt(delay);
            } catch (NumberFormatException e) {
                processingDelay = 150;
            }

            final long round = Math.round((Math.random() * 200) + processingDelay);
            System.out.println("Processing thread sleep of: " + round);
            Thread.sleep(round);
        }

        return name;
    }
}

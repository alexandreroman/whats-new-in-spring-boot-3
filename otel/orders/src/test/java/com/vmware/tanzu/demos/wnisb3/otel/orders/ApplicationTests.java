/*
 * Copyright (c) 2022 VMware, Inc. or its affiliates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vmware.tanzu.demos.wnisb3.otel.orders;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.skyscreamer.jsonassert.JSONCompare.compareJSON;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationTests {
    @Autowired
    private TestRestTemplate client;

    @Test
    void contextLoads() {
    }

    @Test
    void orders() throws JSONException {
        final var order1Json = """
                {
                "orderId": "e5377e96-c6c6-4f00-bdd1-f36efb6b9b6a",
                "customerId": "johndoe",
                "state": "New",
                "dueDate": "2022-11-14T09:13:30Z",
                "itemIds": ["41a1c650-df66-46d0-b7fb-96a117c5dda7", "e5b6da9d-ba51-4119-b527-ace1aaa7985e"]
                }
                """;
        var e = client.getForEntity("/api/v1/orders/e5377e96-c6c6-4f00-bdd1-f36efb6b9b6a", String.class);
        assertThat(e.getStatusCode().is2xxSuccessful()).isTrue();
        var jsonCompareResult = compareJSON(order1Json, e.getBody(), JSONCompareMode.STRICT);
        assertThat(jsonCompareResult.passed()).withFailMessage(jsonCompareResult.getMessage()).isTrue();

        final var order2Json = """
                {
                "orderId": "998d14af-aac1-4082-8194-990a3c24f553",
                "customerId": "bartsimpsons",
                "state": "Canceled",
                "dueDate": "2022-11-25T09:34:30Z",
                "itemIds": ["dc68e695-e8c3-4bc9-9531-28aed4a6ecd6"]
                }
                """;
        e = client.getForEntity("/api/v1/orders/998d14af-aac1-4082-8194-990a3c24f553", String.class);
        assertThat(e.getStatusCode().is2xxSuccessful()).isTrue();
        jsonCompareResult = compareJSON(order2Json, e.getBody(), JSONCompareMode.STRICT);
        assertThat(jsonCompareResult.passed()).withFailMessage(jsonCompareResult.getMessage()).isTrue();
    }

    @Test
    void orderNotFound() {
        final var e = client.getForEntity("/api/v1/orders/notfound", ProblemDetail.class);
        assertThat(e.getStatusCode().is4xxClientError()).isTrue();
        assertThat(e.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON);

        final var detail = e.getBody();
        assertThat(detail.getType()).isEqualTo(URI.create("urn:problem-type:order-not-found"));
    }
}

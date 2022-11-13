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

package com.vmware.tanzu.demos.wnisb3.otel.items;

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
    void items() throws JSONException {
        final var item1Json = """
                {
                "itemId": "41a1c650-df66-46d0-b7fb-96a117c5dda7",
                "title": "Hat: Spring Boot FTW",
                "price": "100"
                }
                """;
        var e = client.getForEntity("/api/v1/items/41a1c650-df66-46d0-b7fb-96a117c5dda7", String.class);
        assertThat(e.getStatusCode().is2xxSuccessful()).isTrue();
        var jsonCompareResult = compareJSON(item1Json, e.getBody(), JSONCompareMode.STRICT);
        assertThat(jsonCompareResult.passed()).withFailMessage(jsonCompareResult.getMessage()).isTrue();

        final var item2Json = """
                {
                "itemId": "e5b6da9d-ba51-4119-b527-ace1aaa7985e",
                "title": "Laptop sticker: I love Java",
                "price": "15"
                }
                """;
        e = client.getForEntity("/api/v1/items/e5b6da9d-ba51-4119-b527-ace1aaa7985e", String.class);
        assertThat(e.getStatusCode().is2xxSuccessful()).isTrue();
        jsonCompareResult = compareJSON(item2Json, e.getBody(), JSONCompareMode.STRICT);
        assertThat(jsonCompareResult.passed()).withFailMessage(jsonCompareResult.getMessage()).isTrue();

        final var item3Json = """
                {
                "itemId": "dc68e695-e8c3-4bc9-9531-28aed4a6ecd6",
                "title": "T-shirt: Kubernetes is boring",
                "price": "27"
                }
                """;
        e = client.getForEntity("/api/v1/items/dc68e695-e8c3-4bc9-9531-28aed4a6ecd6", String.class);
        assertThat(e.getStatusCode().is2xxSuccessful()).isTrue();
        jsonCompareResult = compareJSON(item3Json, e.getBody(), JSONCompareMode.STRICT);
        assertThat(jsonCompareResult.passed()).withFailMessage(jsonCompareResult.getMessage()).isTrue();
    }

    @Test
    void itemNotFound() {
        final var e = client.getForEntity("/api/v1/items/notfound", ProblemDetail.class);
        assertThat(e.getStatusCode().is4xxClientError()).isTrue();
        assertThat(e.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON);

        final var detail = e.getBody();
        assertThat(detail.getType()).isEqualTo(URI.create("urn:problem-type:item-not-found"));
    }
}

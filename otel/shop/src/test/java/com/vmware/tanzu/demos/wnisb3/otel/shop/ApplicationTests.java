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

package com.vmware.tanzu.demos.wnisb3.otel.shop;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.skyscreamer.jsonassert.JSONCompare.compareJSON;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
class ApplicationTests {
    @Autowired
    private TestRestTemplate client;

    @Test
    void contextLoads() {
    }

    @Test
    void indexPage() throws JSONException {
        final var order1Json = """
                {
                "orderId": "e5377e96-c6c6-4f00-bdd1-f36efb6b9b6a",
                "customerId": "foo",
                "state": "New",
                "dueDate": "2022-11-14T09:13:30+00:00",
                "itemIds": ["item1", "item2"]
                }
                """;
        stubFor(get("/api/v1/orders/e5377e96-c6c6-4f00-bdd1-f36efb6b9b6a").willReturn(okJson(order1Json)));

        final var order2Json = """
                {
                "orderId": "998d14af-aac1-4082-8194-990a3c24f553",
                "customerId": "bar",
                "state": "Completed",
                "dueDate": "2022-11-25T09:34:30+00:00",
                "itemIds": ["item3"]
                }
                """;
        stubFor(get("/api/v1/orders/998d14af-aac1-4082-8194-990a3c24f553").willReturn(okJson(order2Json)));

        final var item1Json = """
                {
                "itemId": "item1",
                "title": "Title1",
                "price": "102.3"
                }
                """;
        stubFor(get("/api/v1/items/item1").willReturn(okJson(item1Json)));

        final var item2Json = """
                {
                "itemId": "item2",
                "title": "Title2",
                "price": "23.4"
                }
                """;
        stubFor(get("/api/v1/items/item2").willReturn(okJson(item2Json)));

        final var item3Json = """
                {
                "itemId": "item3",
                "title": "Title3",
                "price": "12.3"
                }
                """;
        stubFor(get("/api/v1/items/item3").willReturn(okJson(item3Json)));

        final var e = client.getForEntity("/", String.class);
        assertThat(e.getStatusCode().is2xxSuccessful()).isTrue();

        final var output = """
                {
                "title": "Welcome to SpringBootShop Tests!",
                "orders": [
                    {
                    "orderId": "e5377e96-c6c6-4f00-bdd1-f36efb6b9b6a",
                    "customerId": "foo",
                    "state": "New",
                    "dueDate": "2022-11-14T09:13:30Z",
                    "items": [
                        {
                        "itemId": "item1",
                        "title": "Title1",
                        "price": "102.3"
                        },
                        {
                        "itemId": "item2",
                        "title": "Title2",
                        "price": "23.4"
                        }
                    ]},
                    {
                    "orderId": "998d14af-aac1-4082-8194-990a3c24f553",
                    "customerId": "bar",
                    "state": "Completed",
                    "dueDate": "2022-11-25T09:34:30Z",
                    "items": [
                        {
                        "itemId": "item3",
                        "title": "Title3",
                        "price": "12.3"
                        }
                    ]}
                ]}
                """;
        final var jsonCompareResult = compareJSON(output, e.getBody(), JSONCompareMode.LENIENT);
        assertThat(jsonCompareResult.passed()).withFailMessage(jsonCompareResult.getMessage()).isTrue();
    }
}

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

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.Random;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Create an in-memory list of items.
     */
    @Bean
    @Qualifier("items")
    Map<String, OrderItem> createItems() {
        final var i1 = new OrderItem("41a1c650-df66-46d0-b7fb-96a117c5dda7", "Hat: Spring Boot FTW", "100");
        final var i2 = new OrderItem("e5b6da9d-ba51-4119-b527-ace1aaa7985e", "Laptop sticker: I love Java", "15");
        final var i3 = new OrderItem("dc68e695-e8c3-4bc9-9531-28aed4a6ecd6", "T-shirt: Kubernetes is boring", "27");
        return Map.of(i1.itemId(), i1, i2.itemId(), i2, i3.itemId(), i3);
    }
}

/**
 * This class is responsible for registering custom metrics.
 */
@Configuration(proxyBeanMethods = false)
class MetricsConf {
    /**
     * Create a counter for the number of times the items service was hit.
     */
    @Bean
    @Qualifier("itemsServiceHitCounter")
    Counter itemsServiceHitCounter(MeterRegistry reg) {
        return Counter.builder("hit.counter").baseUnit("hits").description("Hit counter").tags("page", "items").register(reg);
    }
}

@RestController
class ItemController {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Random random = new Random();
    private final Counter hitCounter;
    private final Map<String, OrderItem> items;

    public ItemController(@Qualifier("items") Map<String, OrderItem> items, @Qualifier("itemsServiceHitCounter") Counter hitCounter) {
        this.hitCounter = hitCounter;
        this.items = items;
    }

    @GetMapping("/api/v1/items/{itemId}")
    OrderItem orders(@PathVariable("itemId") String itemId) throws InterruptedException {
        hitCounter.increment();

        logger.info("Looking up items: {}", itemId);
        final var item = items.get(itemId);

        final var delay = random.nextLong(1000);
        logger.debug("Slowing down items service by {} ms", delay);
        Thread.sleep(delay);

        if (item == null) {
            logger.info("Item not found: {}", itemId);
            // This error is mapped to a 404 HTTP response.
            throw new ItemNotFoundException(itemId);
        }
        logger.info("Item {} found: {}", itemId, item);
        return item;
    }
}


class ItemNotFoundException extends RuntimeException {
    private final String itemId;

    ItemNotFoundException(String itemId) {
        super("Item not found: " + itemId);
        this.itemId = itemId;
    }

    public String getItemId() {
        return itemId;
    }
}

@RestControllerAdvice
class ItemControllerAdvice {
    @ExceptionHandler(ItemNotFoundException.class)
    ProblemDetail handleItemNotFoundException(ItemNotFoundException e) {
        // Map this exception to a RFC 7807 entity (Problem Details for HTTP APIs).
        final var detail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        detail.setTitle(e.getMessage());
        detail.setType(URI.create("urn:problem-type:item-not-found"));
        return detail;
    }
}

record OrderItem(String itemId, String title, String price) {
}

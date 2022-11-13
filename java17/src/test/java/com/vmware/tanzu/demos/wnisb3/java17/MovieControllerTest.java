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

package com.vmware.tanzu.demos.wnisb3.java17;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.List;

import static com.vmware.tanzu.demos.wnisb3.java17.Genre.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MovieControllerTest {
    @Autowired
    private TestRestTemplate client;

    @Test
    void movies() {
        final var e = client.getForEntity("/", Movie[].class);
        assertThat(e.getStatusCode().is2xxSuccessful()).isTrue();

        final var movies = e.getBody();
        assertThat(movies.length).isEqualTo(7);
        assertThat(movies[0]).isEqualTo(new Movie(0, "Iron Man", Action));
        assertThat(movies[1]).isEqualTo(new Movie(1, "Minions", Comedy));
        assertThat(movies[2]).isEqualTo(new Movie(2, "1917", Drama));
        assertThat(movies[3]).isEqualTo(new Movie(3, "Lord of the Rings", Fantasy));
        assertThat(movies[4]).isEqualTo(new Movie(4, "Scream", Horror));
        assertThat(movies[5]).isEqualTo(new Movie(5, "Gone Girl", Mystery));
        assertThat(movies[6]).isEqualTo(new Movie(6, "Tenet", Thriller));
    }
}

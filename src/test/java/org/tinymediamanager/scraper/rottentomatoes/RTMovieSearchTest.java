/*
 * Copyright 2012 - 2016 Manuel Laggner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tinymediamanager.scraper.rottentomatoes;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Assert;
import org.junit.Test;
import org.tinymediamanager.scraper.rottentomatoes.entities.RTMovieSearchResult;
import org.tinymediamanager.scraper.util.ApiKey;

public class RTMovieSearchTest {

  @Test
  public void testMovieSearch() {
    RottenTomatoes rottenTomates = new RottenTomatoes(ApiKey.decryptApikey("O0HBqxsoTCzaT3lTxVxmoMji3yzUuXSRbnMTRLnkHY4="));
    try {
      // rottenTomates.setIsDebug(true);
      RTMovieSearchResult searchResult = rottenTomates.getMovieSearchService().searchMovie("Harry Potter");

      assertThat(searchResult.total).isEqualTo(22);
      assertThat(searchResult.movies.size()).isEqualTo(22);
      assertThat(searchResult.movies.get(0).title).isEqualTo("Harry Potter and the Deathly Hallows - Part 2");
      assertThat(searchResult.movies.get(0).year).isEqualTo(2011);
    }
    catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }
}

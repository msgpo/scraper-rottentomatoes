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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.tinymediamanager.scraper.MediaMetadata;
import org.tinymediamanager.scraper.MediaScrapeOptions;
import org.tinymediamanager.scraper.MediaSearchOptions;
import org.tinymediamanager.scraper.MediaSearchResult;
import org.tinymediamanager.scraper.entities.MediaGenres;
import org.tinymediamanager.scraper.entities.MediaType;
import org.tinymediamanager.scraper.mediaprovider.IMovieMetadataProvider;

public class RottenTomatoesMetadataProviderTest {

  @Test
  public void testSearch() {
    try {
      IMovieMetadataProvider rt = new RottenTomatoesMetadataProvider();
      MediaSearchOptions options = new MediaSearchOptions(MediaType.MOVIE, "12 Monkeys");

      List<MediaSearchResult> results = rt.search(options);
      assertEquals(2, results.size());
    }
    catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  @Test
  public void testScrape() throws Exception {
    try {
      IMovieMetadataProvider rt = new RottenTomatoesMetadataProvider();

      MediaScrapeOptions options = new MediaScrapeOptions(MediaType.MOVIE);
      options.setId(rt.getProviderInfo().getId(), "15508");
      MediaMetadata md = rt.getMetadata(options);
      assertNotNull("MediaMetadata", md);
      assertEquals("title", "Twelve Monkeys (12 Monkeys)", md.getTitle());
      assertEquals("year", 1995, md.getYear());
      assertEquals("rating", 8.8d, md.getRating(), 0.2d);
      assertEquals("plot", "", md.getPlot());
      assertThat(md.getProductionCompanies()).containsOnly("Universal Pictures");
      assertEquals("imdbid", "tt0114746", md.getId(MediaMetadata.IMDB));
      assertEquals("runtime", 130, (int) md.getRuntime());

      assertEquals("genres", 3, md.getGenres().size());
      assertEquals("Drama", true, md.getGenres().contains(MediaGenres.DRAMA));
      assertEquals("Fantasy", true, md.getGenres().contains(MediaGenres.FANTASY));
      assertEquals("Science Fiction", true, md.getGenres().contains(MediaGenres.SCIENCE_FICTION));

      assertEquals("genres", 6, md.getCastMembers().size());
    }
    catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }
}

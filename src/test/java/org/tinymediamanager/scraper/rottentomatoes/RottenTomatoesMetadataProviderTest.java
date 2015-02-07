/*
 * Copyright 2012 - 2015 Manuel Laggner
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

import static org.junit.Assert.*;

import java.util.List;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.junit.Test;
import org.tinymediamanager.scraper.IMovieMetadataProvider;
import org.tinymediamanager.scraper.MediaGenres;
import org.tinymediamanager.scraper.MediaMetadata;
import org.tinymediamanager.scraper.MediaScrapeOptions;
import org.tinymediamanager.scraper.MediaSearchOptions;
import org.tinymediamanager.scraper.MediaSearchOptions.SearchParam;
import org.tinymediamanager.scraper.MediaSearchResult;
import org.tinymediamanager.scraper.MediaType;


@PluginImplementation
public class RottenTomatoesMetadataProviderTest {

  @Test
  public void testSearch() {
    try {
      IMovieMetadataProvider rt = new RottenTomatoesMetadataProvider();
      MediaSearchOptions options = new MediaSearchOptions(MediaType.MOVIE);

      options.set(SearchParam.QUERY, "12 Monkeys");

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
      assertEquals("title", "Twelve Monkeys (12 Monkeys)", md.getStringValue(MediaMetadata.TITLE));
      assertEquals("year", "1995", md.getStringValue(MediaMetadata.YEAR));
      assertEquals("rating", 8.8d, md.getDoubleValue(MediaMetadata.RATING), 0.2d);
      assertEquals("plot", "", md.getStringValue(MediaMetadata.PLOT));
      assertEquals("production company", "Universal Pictures", md.getStringValue(MediaMetadata.PRODUCTION_COMPANY));
      assertEquals("imdbid", "tt0114746", md.getId(MediaMetadata.IMDBID));
      assertEquals("runtime", 130, (int) md.getIntegerValue(MediaMetadata.RUNTIME));

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

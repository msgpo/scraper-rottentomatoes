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

import org.junit.BeforeClass;
import org.junit.Test;
import org.tinymediamanager.scraper.*;
import org.tinymediamanager.scraper.MediaSearchOptions.SearchParam;
import org.tinymediamanager.scraper.mediaprovider.IMovieMetadataProvider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.LogManager;

import static org.junit.Assert.*;

public class RottenTomatoesMetadataProviderTest {
  private static final String CRLF = "\n";

  @BeforeClass
  public static void setUp() {
    StringBuilder config = new StringBuilder("handlers = java.util.logging.ConsoleHandler\n");
    config.append(".level = ALL").append(CRLF);
    config.append("java.util.logging.ConsoleHandler.level = ALL").append(CRLF);
    // Only works with Java 7 or later
    config.append("java.util.logging.SimpleFormatter.format = [%1$tH:%1$tM:%1$tS %4$6s] %2$s - %5$s %6$s%n").append(CRLF);
    // Exclude http logging
    config.append("sun.net.www.protocol.http.HttpURLConnection.level = OFF").append(CRLF);
    InputStream ins = new ByteArrayInputStream(config.toString().getBytes());
    try {
      LogManager.getLogManager().readConfiguration(ins);
    }
    catch (IOException ignored) {
    }
  }

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
      assertEquals("imdbid", "tt0114746", md.getId(MediaMetadata.IMDB));
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

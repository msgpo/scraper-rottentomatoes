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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tinymediamanager.scraper.rottentomatoes.entities.RTMovieInfo;
import org.tinymediamanager.scraper.util.ApiKey;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

import static org.assertj.core.api.Assertions.assertThat;

public class RTMovieInfoTest {
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
  public void testMovieInfo() {
    RottenTomatoes rottenTomates = new RottenTomatoes(ApiKey.decryptApikey("O0HBqxsoTCzaT3lTxVxmoMji3yzUuXSRbnMTRLnkHY4="));
    try {
      // rottenTomates.setIsDebug(true);
      RTMovieInfo movieInfo = rottenTomates.getMovieInfoService().getMovieInfo(770672122);

      assertThat(movieInfo.id).isEqualTo(770672122);
      assertThat(movieInfo.title).isEqualTo("Toy Story 3");
      assertThat(movieInfo.year).isEqualTo(2010);
      assertThat(movieInfo.genres.size()).isEqualTo(4);
      assertThat(movieInfo.genres.get(0)).isEqualTo("Animation");
      assertThat(movieInfo.mpaaRating).isEqualTo("G");
      assertThat(movieInfo.runtime).isEqualTo(103);
      assertThat(movieInfo.releaseDates.dvd).isEqualTo("2010-11-02");
      assertThat(movieInfo.ratings.audience_score).isBetween(85, 95); // ratings may change every day
      assertThat(movieInfo.synopsis).startsWith("\"Toy Story 3\" welcomes Woody, Buzz and the whole gang back to the big screen as Andy prepares ");
      assertThat(movieInfo.abridgedCast.size()).isEqualTo(5);
//      assertThat(movieInfo.abridgedCast.get(1).name).isEqualTo("Tom Hanks");
//      assertThat(movieInfo.abridgedCast.get(1).characters.size()).isEqualTo(1);
//      assertThat(movieInfo.abridgedCast.get(1).characters.get(0)).isEqualTo("Woody");
      assertThat(movieInfo.studio).isEqualTo("Walt Disney Pictures");
      assertThat(movieInfo.alternateIds.imdb).isEqualTo("0435761");
      assertThat(movieInfo.posters.detailed).isEqualTo("http://resizing.flixster.com/AhKHxRwazY3brMINzfbnx-A8T9c=/54x80/dkpu1ddg7pbsk.cloudfront.net/movie/11/13/43/11134356_ori.jpg");
      assertThat(movieInfo.links.cast).isEqualTo("http://api.rottentomatoes.com/api/public/v1.0/movies/770672122/cast.json");
    }
    catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }

    // along came polly
    try {
      // rottenTomates.setIsDebug(true);
      RTMovieInfo movieInfo = rottenTomates.getMovieInfoService().getMovieInfo(10982);

      assertThat(movieInfo.id).isEqualTo(10982);
      assertThat(movieInfo.title).isEqualTo("Along Came Polly");
      assertThat(movieInfo.year).isEqualTo(2004);
    }
    catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  @Test
  public void testMovieAlias() {
    RottenTomatoes rottenTomates = new RottenTomatoes(ApiKey.decryptApikey("O0HBqxsoTCzaT3lTxVxmoMji3yzUuXSRbnMTRLnkHY4="));
    try {
      rottenTomates.setIsDebug(true);
      RTMovieInfo movieInfo = rottenTomates.getMovieAliasService().getMovieInfo("0435761");

      assertThat(movieInfo.id).isEqualTo(770672122);
      assertThat(movieInfo.title).isEqualTo("Toy Story 3");
      assertThat(movieInfo.year).isEqualTo(2010);
      assertThat(movieInfo.genres.size()).isEqualTo(4);
      assertThat(movieInfo.genres.get(0)).isEqualTo("Animation");
      assertThat(movieInfo.mpaaRating).isEqualTo("G");
      assertThat(movieInfo.runtime).isEqualTo(103);
      assertThat(movieInfo.releaseDates.dvd).isEqualTo("2010-11-02");
      assertThat(movieInfo.ratings.audience_score).isBetween(85, 95); // ratings may change every day
      assertThat(movieInfo.synopsis).startsWith("\"Toy Story 3\" welcomes Woody, Buzz and the whole gang back to the big screen as Andy prepares ");
      assertThat(movieInfo.abridgedCast.size()).isEqualTo(5);
      assertThat(movieInfo.abridgedCast.get(1).name).isEqualTo("Tom Hanks");
      assertThat(movieInfo.abridgedCast.get(1).characters.size()).isEqualTo(1);
      assertThat(movieInfo.abridgedCast.get(1).characters.get(0)).isEqualTo("Woody");
      assertThat(movieInfo.studio).isEqualTo("Walt Disney Pictures");
      assertThat(movieInfo.alternateIds.imdb).isEqualTo("0435761");
      assertThat(movieInfo.posters.detailed).isEqualTo("http://content6.flixster.com/movie/11/13/43/11134356_tmb.jpg");
      assertThat(movieInfo.links.cast).isEqualTo("http://api.rottentomatoes.com/api/public/v1.0/movies/770672122/cast.json");
    }
    catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }
}

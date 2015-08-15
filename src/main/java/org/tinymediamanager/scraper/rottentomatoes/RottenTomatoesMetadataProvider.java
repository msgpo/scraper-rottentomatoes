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

import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinymediamanager.scraper.IMovieMetadataProvider;
import org.tinymediamanager.scraper.MediaCastMember;
import org.tinymediamanager.scraper.MediaCastMember.CastType;
import org.tinymediamanager.scraper.MediaGenres;
import org.tinymediamanager.scraper.MediaMetadata;
import org.tinymediamanager.scraper.MediaProviderInfo;
import org.tinymediamanager.scraper.MediaScrapeOptions;
import org.tinymediamanager.scraper.MediaSearchOptions;
import org.tinymediamanager.scraper.MediaSearchResult;
import org.tinymediamanager.scraper.MediaType;
import org.tinymediamanager.scraper.MetadataUtil;
import org.tinymediamanager.scraper.UnsupportedMediaTypeException;
import org.tinymediamanager.scraper.rottentomatoes.entities.RTCast;
import org.tinymediamanager.scraper.rottentomatoes.entities.RTDirector;
import org.tinymediamanager.scraper.rottentomatoes.entities.RTMovieInfo;
import org.tinymediamanager.scraper.rottentomatoes.entities.RTMovieSearchResult;
import org.tinymediamanager.scraper.util.ApiKey;
import org.tinymediamanager.scraper.util.RingBuffer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The class RottenTomatoesMetadataProvider. A scraper for Rotten tomatoes
 * 
 * @author Manuel Laggner
 */
@PluginImplementation
public class RottenTomatoesMetadataProvider implements IMovieMetadataProvider {
  private static final Logger            LOGGER            = LoggerFactory.getLogger(RottenTomatoesMetadataProvider.class);
  private static final RingBuffer<Long>  connectionCounter = new RingBuffer<Long>(5);
  private static final MediaProviderInfo providerInfo      = createMediaProviderInfo();
  private static RottenTomatoes          api;

  public RottenTomatoesMetadataProvider(){
  }

  private static MediaProviderInfo createMediaProviderInfo(){
    MediaProviderInfo providerInfo =new MediaProviderInfo("rottentomatoes", "Rotten Tomatoes",
            "<html><h3>Rotten Tomatoes</h3><br />An american movie database.<br />Does not provide plot for older movies via the API.<br /><br />Available languages: EN</html>",
            RottenTomatoesMetadataProvider.class.getResource("/rottentomatoes_com.png"));

    return providerInfo;
  }

  private static synchronized void initRottenTomatoesApiInstance() throws Exception {
    // create a new instance of the rottentomatoes api
    if (api == null) {
      try {
        api = new RottenTomatoes(ApiKey.decryptApikey("O0HBqxsoTCzaT3lTxVxmoMji3yzUuXSRbnMTRLnkHY4="));
      }
      catch (Exception e) {
        LOGGER.error("RottenTomatoesMetadataProvider", e);
        throw e;
      }
    }
  }

  @Override
  public MediaProviderInfo getProviderInfo() {
    return providerInfo;
  }

  @Override
  public MediaMetadata getMetadata(MediaScrapeOptions options) throws Exception {
    LOGGER.debug("getMetadata() " + options.toString());

    // init API - lazy loading
    initRottenTomatoesApiInstance();

    if (options.getType() != MediaType.MOVIE) {
      throw new UnsupportedMediaTypeException(options.getType());
    }

    // check if there is a md in the result
    if (options.getResult() != null && options.getResult().getMediaMetadata() != null) {
      LOGGER.debug("RottenTomatoes: getMetadata from cache: " + options.getResult());
      return options.getResult().getMediaMetadata();
    }

    // get ids to scrape
    MediaMetadata md = new MediaMetadata(providerInfo.getId());

    int rottenId = 0;

    // rottenId from searchResult
    if (options.getResult() != null) {
      rottenId = Integer.parseInt(options.getResult().getId());
    }

    // rottenId from option
    if (rottenId == 0) {
      try {
        rottenId = Integer.parseInt(options.getId(providerInfo.getId()));
      }
      catch (Exception e) {
      }
    }

    // imdbId from option
    String imdbId = options.getImdbId();
    if (rottenId == 0 && !MetadataUtil.isValidImdbId(imdbId)) {
      LOGGER.warn("not possible to scrape from RottenTomatoes - no rottenId/imdbId found");
      return md;
    }

    // scrape
    LOGGER.debug("RottenTomatoes: getMetadata: rottenId = " + rottenId + "; imdbId = " + imdbId);
    RTMovieInfo movie = null;
    synchronized (api) {
      trackConnections();
      if (rottenId == 0 && MetadataUtil.isValidImdbId(imdbId)) {
        try {
          movie = api.getMovieAliasService().getMovieInfo(imdbId);
        }
        catch (Exception e) {
          LOGGER.warn("problem getting data vom RottenTomatoes: " + e.getMessage());
        }
      }
      if (movie == null && rottenId != 0) {
        try {
          movie = api.getMovieInfoService().getMovieInfo(rottenId);
        }
        catch (Exception e) {
          LOGGER.warn("problem getting data vom RottenTomatoes: " + e.getMessage());
        }
      }

      if (movie == null) {
        LOGGER.warn("no result found");
        return md;
      }
    }

    md.setId(providerInfo.getId(), movie.id);
    if (StringUtils.isNotBlank(movie.alternateIds.imdb)) {
      md.setId(MediaMetadata.IMDB, "tt" + movie.alternateIds.imdb);
    }
    md.storeMetadata(MediaMetadata.PLOT, movie.synopsis);
    md.storeMetadata(MediaMetadata.TITLE, movie.title);
    md.storeMetadata(MediaMetadata.YEAR, movie.year);
    md.storeMetadata(MediaMetadata.PRODUCTION_COMPANY, movie.studio);
    md.storeMetadata(MediaMetadata.RATING, movie.ratings.audience_score / 10d);
    md.storeMetadata(MediaMetadata.RUNTIME, movie.runtime);

    // genres
    for (String genre : movie.genres) {
      if ("Science Fiction & Fantasy".equals(genre)) {
        md.addGenre(MediaGenres.FANTASY);
        md.addGenre(MediaGenres.SCIENCE_FICTION);
      }
      else {
        MediaGenres g = MediaGenres.getGenre(genre);
        if (g != null) {
          md.addGenre(g);
        }
      }
    }

    // cast
    for (RTCast rtCast : movie.abridgedCast) {
      MediaCastMember cm = new MediaCastMember();
      cm.setName(rtCast.name);
      cm.setType(CastType.ACTOR);

      String roles = "";
      for (String character : rtCast.characters) {
        if (StringUtils.isNotBlank(roles)) {
          roles += ", ";
        }
        roles += character;
      }
      cm.setCharacter(roles);

      md.addCastMember(cm);
    }

    // directors
    for (RTDirector person : movie.abridgedDirectors) {
      MediaCastMember cm = new MediaCastMember();
      cm.setType(CastType.DIRECTOR);
      cm.setName(person.name);

      md.addCastMember(cm);
    }

    return md;
  }

  @Override
  public List<MediaSearchResult> search(MediaSearchOptions query) throws Exception {
    // init API - lazy loading
    initRottenTomatoesApiInstance();

    // check type
    if (query.getMediaType() == MediaType.MOVIE) {
      return searchMovies(query);
    }

    throw new UnsupportedMediaTypeException(query.getMediaType());
  }

  public List<MediaSearchResult> searchMovies(MediaSearchOptions query) throws Exception {
    LOGGER.debug("search() " + query.toString());
    List<MediaSearchResult> resultList = new ArrayList<MediaSearchResult>();
    String searchString = "";

    if (StringUtils.isEmpty(searchString) && StringUtils.isNotEmpty(query.get(MediaSearchOptions.SearchParam.QUERY))) {
      searchString = query.get(MediaSearchOptions.SearchParam.QUERY);
    }

    if (StringUtils.isEmpty(searchString)) {
      LOGGER.debug("RT Scraper: empty searchString");
      return resultList;
    }

    searchString = MetadataUtil.removeNonSearchCharacters(searchString);

    // begin search
    LOGGER.info("========= BEGIN RT Scraper Search for: " + searchString);

    RTMovieSearchResult searchResult = null;
    String imdbId = "";
    synchronized (api) {
      // 1. try with IMDBid
      if (StringUtils.isNotEmpty(query.get(MediaSearchOptions.SearchParam.IMDBID))) {
        imdbId = query.get(MediaSearchOptions.SearchParam.IMDBID);
        trackConnections();
        try {
          searchResult = api.getMovieSearchService().searchMovie(imdbId);
        }
        catch (Exception e) {
          LOGGER.warn("problem getting data vom rt: " + e.getMessage());
        }
      }
      // 2. try to search with searchString
      if (searchResult == null || searchResult.total == 0) {
        trackConnections();
        try {
          searchResult = api.getMovieSearchService().searchMovie(searchString);
        }
        catch (Exception e) {
          LOGGER.warn("problem getting data vom rt: " + e.getMessage());
        }
      }
    }

    if (searchResult != null) {
      LOGGER.info("found " + searchResult.total + " results");
    }

    if (searchResult == null || searchResult.total == 0) {
      return resultList;
    }

    for (RTMovieInfo movie : searchResult.movies) {
      if (movie == null) {
        continue;
      }

      MediaSearchResult sr = new MediaSearchResult(providerInfo.getId());
      sr.setId(Integer.toString(movie.id));
      sr.setTitle(movie.title);

      // alternate IDs
      if (StringUtils.isNotBlank(movie.alternateIds.imdb)) {
        sr.setIMDBId(movie.alternateIds.imdb);
      }

      // poster
      if (movie.posters != null) {
        String posterUrl = movie.posters.detailed;
        if (StringUtils.isBlank(posterUrl)) {
          posterUrl = movie.posters.original;
        }
        if (StringUtils.isNotBlank(posterUrl)) {
          sr.setPosterUrl(posterUrl.replace("_tmb.", "_det."));
        }
      }

      sr.setYear(Integer.toString(movie.year));
      // parse release date to year
      if (StringUtils.isBlank(sr.getYear()) && movie.releaseDates != null) {
        String releaseDate = movie.releaseDates.theater;
        if (StringUtils.isBlank(releaseDate)) {
          releaseDate = movie.releaseDates.dvd;
        }
        if (releaseDate != null && releaseDate.length() > 3) {
          sr.setYear(releaseDate.substring(0, 4));
        }
      }

      // populate extra args
      MetadataUtil.copySearchQueryToSearchResult(query, sr);

      if (imdbId.equals(sr.getIMDBId())) {
        // perfect match
        sr.setScore(1);
      }
      else {
        // compare score based on names
        sr.setScore(MetadataUtil.calculateScore(searchString, movie.title));
      }

      resultList.add(sr);
    }

    Collections.sort(resultList);
    Collections.reverse(resultList);

    return resultList;
  }

  /*
   * 5 calls per 1 second
   */
  private void trackConnections() {
    Long currentTime = System.currentTimeMillis();
    if (connectionCounter.count() == connectionCounter.maxSize()) {
      Long oldestConnection = connectionCounter.getTailItem();
      if (oldestConnection > (currentTime - 1000)) {
        LOGGER.debug("connection limit reached, throttling " + connectionCounter);
        try {
          Thread.sleep(1100 - (currentTime - oldestConnection));
        }
        catch (InterruptedException e) {
          LOGGER.warn(e.getMessage());
        }
      }
    }

    currentTime = System.currentTimeMillis();
    connectionCounter.add(currentTime);
  }
}

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
package org.tinymediamanager.scraper.rottentomatoes;/*
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

import com.google.gson.*;
import com.google.gson.internal.bind.DateTypeAdapter;
import org.tinymediamanager.scraper.rottentomatoes.services.MovieAliasService;
import org.tinymediamanager.scraper.rottentomatoes.services.MovieInfoService;
import org.tinymediamanager.scraper.rottentomatoes.services.MovieSearchService;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

import java.lang.reflect.Type;
import java.util.Date;

public class RottenTomatoes {
  // the base API url
  public static final String API_URL       = "http://api.rottentomatoes.com/api/public/v1.0/";
  // the api key query parameter; hast to be supplied at all calls
  public static final String PARAM_API_KEY = "apikey";

  private RestAdapter        restAdapter;
  private boolean            isDebug;
  private String             apiKey;

  public RottenTomatoes(String apiKey) {
    this.apiKey = apiKey;
  }

  /**
   * Set the {@link retrofit.RestAdapter} log level.
   *
   * @param isDebug
   *          If true, the log level is set to {@link retrofit.RestAdapter.LogLevel#FULL}. Otherwise {@link retrofit.RestAdapter.LogLevel#NONE}.
   */
  public RottenTomatoes setIsDebug(boolean isDebug) {
    this.isDebug = isDebug;
    if (restAdapter != null) {
      restAdapter.setLogLevel(isDebug ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE);
    }
    return this;
  }

  /**
   * Create a new {@link retrofit.RestAdapter.Builder}. Override this to e.g. set your own client or executor.
   *
   * @return A {@link retrofit.RestAdapter.Builder} with no modifications.
   */
  protected RestAdapter.Builder newRestAdapterBuilder() {
    return new RestAdapter.Builder();
  }

  /**
   * Return the current {@link retrofit.RestAdapter} instance. If none exists (first call), builds a new one.
   */
  protected RestAdapter getRestAdapter() {
    if (restAdapter == null) {
      RestAdapter.Builder builder = newRestAdapterBuilder();
      builder.setEndpoint(API_URL);
      builder.setConverter(new GsonConverter(getGsonBuilder().create()));
      builder.setRequestInterceptor(new RequestInterceptor() {
        @Override
        public void intercept(RequestInterceptor.RequestFacade requestFacade) {
          requestFacade.addQueryParam(PARAM_API_KEY, apiKey);
        }
      });
      if (isDebug) {
        builder.setLogLevel(RestAdapter.LogLevel.FULL);
      }
      restAdapter = builder.build();
    }
    return restAdapter;
  }

  protected GsonBuilder getGsonBuilder() {
    GsonBuilder builder = new GsonBuilder();
    // class types
    builder.registerTypeAdapter(Integer.class, new JsonDeserializer<Integer>() {
      @Override
      public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
          return Integer.valueOf(json.getAsInt());
        }
        catch (NumberFormatException e) {
          return null;
        }
      }
    });
    builder.registerTypeAdapter(Date.class, new DateTypeAdapter());
    builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
    return builder;
  }

  public MovieInfoService getMovieInfoService() {
    return getRestAdapter().create(MovieInfoService.class);
  }

  public MovieAliasService getMovieAliasService() {
    return getRestAdapter().create(MovieAliasService.class);
  }

  public MovieSearchService getMovieSearchService() {
    return getRestAdapter().create(MovieSearchService.class);
  }
}

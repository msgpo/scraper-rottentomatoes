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
package org.tinymediamanager.scraper.rottentomatoes.entities;

import java.util.Arrays;
import java.util.List;

public class RTMovieInfo {
  public Integer          id = 0;
  public String           title = "";
  public Integer          year = 0;
  public List<String>     genres = Arrays.asList();
  public String           mpaaRating = "";
  public Integer          runtime = 0;
  public String           criticsConsensus = "";
  public RTReleaseDate    releaseDates = new RTReleaseDate();
  public RTRating         ratings = new RTRating();
  public String           synopsis = "";
  public RTPosters        posters = new RTPosters();
  public List<RTCast>     abridgedCast = Arrays.asList();
  public List<RTDirector> abridgedDirectors = Arrays.asList();
  public String           studio = "";
  public RTAlternateIds   alternateIds = new RTAlternateIds();
  public RTLinks          links = new RTLinks();
}

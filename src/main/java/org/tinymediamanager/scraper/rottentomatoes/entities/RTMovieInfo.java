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

import java.util.List;

public class RTMovieInfo {
  public Integer          id;
  public String           title;
  public Integer          year;
  public List<String>     genres;
  public String           mpaaRating;
  public Integer          runtime;
  public String           criticsConsensus;
  public RTReleaseDate    releaseDates;
  public RTRating         ratings;
  public String           synopsis;
  public RTPosters        posters;
  public List<RTCast>     abridgedCast;
  public List<RTDirector> abridgedDirectors;
  public String           studio;
  public RTAlternateIds   alternateIds;
  public RTLinks          links;
}

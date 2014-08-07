/*
 * SonarQube Java
 * Copyright (C) 2012 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.java;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.BatchExtension;
import org.sonar.api.batch.ProjectClasspath;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.config.Settings;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;

public class JavaClasspath implements BatchExtension {
  public static final String SONAR_JAVA_BINARIES = "sonar.java.binaries";
  public static final String SONAR_JAVA_LIBRARIES = "sonar.java.libraries";
  private static final char SEPARATOR = ',';
  private List<File> binaries;
  private List<File> libraries;
  private List<File> elements;


  public JavaClasspath(Settings settings, FileSystem fileSystem, @Nullable ProjectClasspath projectClasspath) {
    binaries = getFilesFromProperty(SONAR_JAVA_BINARIES, settings, fileSystem.baseDir());
    libraries = getFilesFromProperty(SONAR_JAVA_LIBRARIES, settings, fileSystem.baseDir());
    if(projectClasspath == null || !binaries.isEmpty() || !libraries.isEmpty()) {
      elements = Lists.newArrayList(binaries);
      elements.addAll(libraries);
    }else {
      elements = projectClasspath.getElements();
    }
  }

  private List<File> getFilesFromProperty(String property, Settings settings, File baseDir) {
    List<File> result = Lists.newArrayList();
    String fileList = settings.getString(property);
    if (StringUtils.isNotEmpty(fileList)) {
      List<String> fileNames = Lists.newArrayList(StringUtils.split(fileList, SEPARATOR));
      for (String fileName : fileNames) {
        result.add(new File(baseDir, fileName));
      }
    }
    return result;
  }

  public List<File> getElements() {
    return elements;
  }

  public static List<PropertyDefinition> getProperties() {
    ImmutableList.Builder<PropertyDefinition> extensions = ImmutableList.builder();
    extensions.add(PropertyDefinition.builder(SONAR_JAVA_BINARIES)
        .description("Comma-separated paths to directories containing the binary files (directories with class files).")
        .hidden()
        .build()
    );
    extensions.add(PropertyDefinition.builder(SONAR_JAVA_LIBRARIES)
        .hidden()
        .build()
    );
    return extensions.build();
  }

  public List<File> getBinaryDirs() {
    return binaries;
  }

  public List<File> getLibraries() {
    return libraries;
  }
}
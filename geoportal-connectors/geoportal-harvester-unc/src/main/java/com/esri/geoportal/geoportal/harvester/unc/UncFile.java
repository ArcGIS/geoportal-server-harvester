/*
 * Copyright 2016 Esri, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.esri.geoportal.geoportal.harvester.unc;

import com.esri.geoportal.harvester.api.base.SimpleDataReference;
import com.esri.geoportal.harvester.api.mime.MimeType;
import com.esri.geoportal.harvester.api.mime.MimeTypeUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Date;
import org.apache.commons.io.IOUtils;

/**
 * UNC file.
 */
/*package*/ class UncFile {
  private final UncBroker broker;
  private final File file;

  /**
   * Creates instance of UNC file.
   * @param broker broker
   * @param file file
   */
  public UncFile(UncBroker broker, File file) {
    this.broker = broker;
    this.file = file;
  }

  /**
   * Reads content.
   * @return content reference
   * @throws IOException if reading content fails
   * @throws URISyntaxException if file url is an invalid URI
   */
  public SimpleDataReference readContent() throws IOException, URISyntaxException {
    Date lastModifiedDate = readLastModifiedDate();
    MimeType contentType = readContentType();
    try (InputStream input = new FileInputStream(file)) {
      return new SimpleDataReference(broker.getBrokerUri(), file.getAbsolutePath(), lastModifiedDate, file.toURI(), IOUtils.toByteArray(input), contentType);
    }
  }

  /**
   * Reads last modified date.
   * @return last modified date
   */
  private Date readLastModifiedDate() {
    return new Date(file.lastModified());
  }
  
  /**
   * Reads content type.
   * @return content type or <code>null</code> if unable to read content type
   */
  private MimeType readContentType() {
    try {
      String strFileUrl = file.getAbsolutePath();
      int lastDotIndex = strFileUrl.lastIndexOf(".");
      String ext = lastDotIndex>=0? strFileUrl.substring(lastDotIndex+1): "";
      return MimeTypeUtils.mapExtension(ext);
    } catch (Exception ex) {
      return null;
    }
  }
  
  @Override
  public String toString() {
    return file.toString();
  }
}

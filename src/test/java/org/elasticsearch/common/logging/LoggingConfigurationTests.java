/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.common.logging;

import com.carrotsearch.ant.tasks.junit4.dependencies.com.google.common.io.Files;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.elasticsearch.common.logging.log4j.Log4jESLogger;
import org.elasticsearch.common.logging.log4j.Log4jESLoggerFactory;
import org.elasticsearch.common.logging.log4j.LogConfigurator;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.test.ElasticsearchTestCase;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;

import static org.hamcrest.Matchers.notNullValue;

/**
 *
 */
public class LoggingConfigurationTests extends ElasticsearchTestCase {

    // @AwaitsFix(bugUrl = "relates to commit 4ebbb657")
    @Test
    public void testMultipleConfigs() throws Exception {
        File configDir = resolveConfigDir();
        logger.info("Using config directory: {}", configDir.getAbsolutePath());
        File loggingFile = new File(configDir, "logging.yml");
        logger.info("Contents of {}: {}", loggingFile, Files.toString(loggingFile, UTF8));
        Settings settings = ImmutableSettings.builder()
                .put("path.conf", configDir.getAbsolutePath())
                .build();
        logger.info("LogConfigurator Settings: {}", settings.getAsMap());
        LogConfigurator.configure(settings);

        ESLogger esLogger = Log4jESLoggerFactory.getLogger("first");
        Logger logger = ((Log4jESLogger) esLogger).logger();
        this.logger.info("Found following appenders:");
        for (Enumeration allAppenders = logger.getAllAppenders(); allAppenders.hasMoreElements();) {
            Appender appender = (Appender) allAppenders.nextElement();
            this.logger.info("Found appender: {}", appender.getName());
        }
        this.logger.info("End of found appenders.");
        Appender appender = logger.getAppender("console1");
        assertThat(appender, notNullValue());

        esLogger = Log4jESLoggerFactory.getLogger("second");
        logger = ((Log4jESLogger) esLogger).logger();
        appender = logger.getAppender("console2");
        assertThat(appender, notNullValue());

        esLogger = Log4jESLoggerFactory.getLogger("third");
        logger = ((Log4jESLogger) esLogger).logger();
        appender = logger.getAppender("console3");
        assertThat(appender, notNullValue());
    }

    private static File resolveConfigDir() throws Exception {
        URL url = LoggingConfigurationTests.class.getResource("config");
        return new File(url.toURI());
    }
}

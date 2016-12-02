/*
 * Copyright 2016 the original author or authors.
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

package org.gradle.api.tasks

import org.gradle.integtests.fixtures.AbstractIntegrationSpec
import org.gradle.integtests.fixtures.LocalBuildCacheFixture

class CachedTaskExecutionErrorHandlingIntegrationTest extends AbstractIntegrationSpec implements LocalBuildCacheFixture {

    def setup() {
        file("init-cache.gradle") << """
            import org.gradle.caching.*
            import org.gradle.caching.internal.*

            buildCache {
                useCacheFactory { startParameter ->
                    return new BuildCache() {
                        @Override
                        boolean load(BuildCacheKey key, BuildCacheEntryReader reader) throws BuildCacheException {
                            if (startParameter.systemPropertiesArgs.containsKey("fail")) {
                                throw new BuildCacheException("Unable to read " + key)
                            } else {
                                return false
                            }
                        }
            
                        @Override
                        void store(BuildCacheKey key, BuildCacheEntryWriter writer) throws BuildCacheException {
                            if (startParameter.systemPropertiesArgs.containsKey("fail")) {
                                throw new BuildCacheException("Unable to write " + key)
                            }
                        }
            
                        @Override
                        String getDescription() {
                            return "Failing cache backend"
                        }
            
                        @Override
                        void close() throws BuildCacheException {
                        }
                    }
                }
            }
        """

        buildFile << """
            apply plugin: "java"
        """

        file("src/main/java/Hello.java") << "public class Hello {}"
    }

    def "cache switches off after third error for the current build"() {
        def lines
        when:
        setupExecuter()
        succeeds "assemble", "-Dfail"
        lines = output.readLines()

        then:
        lines.any { it.matches(/Could not load cache entry .*: Unable to read .*/) }
        lines.any { it.matches(/Could not store cache entry .*: Unable to write .*/) }
        lines.any { it.matches(/Failing cache backend is now disabled because 3 errors were encountered/) }
        lines.any { it.matches(/Failing cache backend was disabled during the build after encountering 3 errors\./) }

        expect:
        succeeds "clean"

        when:
        setupExecuter()
        succeeds "assemble"
        lines = output.readLines()

        then:
        ! lines.any { it.matches(/Could not load cache entry .*: Unable to read .*/) }
        ! lines.any { it.matches(/Could not store cache entry .*: Unable to write .*/) }
        ! lines.any { it.matches(/Failing cache backend is now disabled because 3 errors were encountered/) }
        ! lines.any { it.matches(/Failing cache backend was disabled during the build after encountering 3 errors\./) }
    }

    private void setupExecuter() {
        // We need this so that we can capture the output produced after the build has finished
        executer.requireGradleDistribution()
        executer.withBuildCacheEnabled()
            .withArgument "-I" withArgument "init-cache.gradle"
    }
}

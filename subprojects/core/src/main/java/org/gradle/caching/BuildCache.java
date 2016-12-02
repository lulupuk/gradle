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

package org.gradle.caching;

import org.gradle.api.Incubating;

import java.io.Closeable;
import java.io.IOException;

/**
 * Cache protocol interface to be implemented by build cache backends.
 *
 * @since 3.3
 */
@Incubating
public interface BuildCache extends Closeable {
    /**
     * Load the cached entry corresponding to the given cache key. The {@code reader} will be called if an entry is found in the cache.
     *
     * @param key the cache key.
     * @param reader the reader to read the data corresponding to the cache key.
     * @return {@code true} if an entry was found, {@code false} otherwise.
     * @throws BuildCacheException if the cache fails to load a cache entry for the given key
     */
    boolean load(BuildCacheKey key, BuildCacheEntryReader reader) throws BuildCacheException;

    /**
     * Store the cache entry with the given cache key. The {@code writer} will be called to actually write the data.
     *
     * @param key the cache key.
     * @param writer the writer to write the data corresponding to the cache key.
     * @throws BuildCacheException if the cache fails to store a cache entry for the given key
     */
    void store(BuildCacheKey key, BuildCacheEntryWriter writer) throws BuildCacheException;

    /**
     * Returns a description for the cache.
     */
    String getDescription();

    /**
     * Clean up any resources held by the cache once it's not used anymore.
     *
     * @throws IOException if the cache fails to close cleanly.
     */
    @Override
    void close() throws IOException;
}

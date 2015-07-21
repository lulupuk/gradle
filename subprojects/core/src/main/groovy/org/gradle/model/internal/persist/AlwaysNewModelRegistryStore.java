/*
 * Copyright 2014 the original author or authors.
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

package org.gradle.model.internal.persist;

import org.gradle.api.internal.project.ProjectIdentifier;
import org.gradle.model.internal.inspect.ModelRuleExtractor;
import org.gradle.model.internal.registry.DefaultModelRegistry;
import org.gradle.model.internal.registry.ModelRegistry;

public class AlwaysNewModelRegistryStore implements ModelRegistryStore {

    private final ModelRuleExtractor extractor;
    private final String documentationLocation;

    public AlwaysNewModelRegistryStore(ModelRuleExtractor extractor, String documentationLocation) {
        this.extractor = extractor;
        this.documentationLocation = documentationLocation;
    }

    @Override
    public ModelRegistry get(ProjectIdentifier projectIdentifier) {
        return new DefaultModelRegistry(extractor, documentationLocation);
    }
}

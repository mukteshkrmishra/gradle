/*
 * Copyright 2013 the original author or authors.
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
package org.gradle.api.internal.artifacts.repositories.resolver;

import com.google.common.collect.ImmutableList;
import groovy.lang.Closure;
import org.gradle.api.artifacts.ClientModule;
import org.gradle.api.artifacts.ComponentMetadataDetails;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.ModuleVersionSelector;
import org.gradle.api.internal.artifacts.DefaultModuleVersionIdentifier;
import org.gradle.api.internal.artifacts.dsl.dependencies.DependencyFactory;
import org.gradle.api.internal.artifacts.ivyservice.moduleconverter.dependencies.DependencyDescriptorFactory;
import org.gradle.api.internal.attributes.ImmutableAttributes;
import org.gradle.internal.component.external.model.AbstractMutableModuleComponentResolveMetadata;
import org.gradle.internal.component.model.DependencyMetadata;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ComponentMetadataDetailsAdapter implements ComponentMetadataDetails {
    private final AbstractMutableModuleComponentResolveMetadata metadata;
    private final DependencyFactory dependencyFactory;
    private final DependencyDescriptorFactory dependencyDescriptorFactory;
    private List<ModuleVersionIdentifier> dependencies;

    public ComponentMetadataDetailsAdapter(AbstractMutableModuleComponentResolveMetadata metadata, DependencyFactory dependencyFactory, DependencyDescriptorFactory dependencyDescriptorFactory) {
        this.metadata = metadata;
        this.dependencyFactory = dependencyFactory;
        this.dependencyDescriptorFactory = dependencyDescriptorFactory;
    }

    public ModuleVersionIdentifier getId() {
        return metadata.getId();
    }

    public boolean isChanging() {
        return metadata.isChanging();
    }

    public String getStatus() {
        return metadata.getStatus();
    }

    public List<String> getStatusScheme() {
        return metadata.getStatusScheme();
    }

    public List<ModuleVersionIdentifier> getDependencies() {
        if (dependencies == null) {
            List<ModuleVersionIdentifier> dependenciesAsModuleVersionIdentifiers = new ArrayList<ModuleVersionIdentifier>();
            for (DependencyMetadata dependency : metadata.getDependencies()) {
                ModuleVersionSelector requested = dependency.getRequested();
                dependenciesAsModuleVersionIdentifiers.add(new DefaultModuleVersionIdentifier(requested.getGroup(), requested.getName(), requested.getVersion()));
            }
            dependencies = ImmutableList.copyOf(dependenciesAsModuleVersionIdentifiers);
        }
        return dependencies;
    }

    public void setChanging(boolean changing) {
        metadata.setChanging(changing);
    }

    public void setStatus(String status) {
        metadata.setStatus(status);
    }

    public void setStatusScheme(List<String> statusScheme) {
        metadata.setStatusScheme(statusScheme);
    }

    public void removeAllDependencies() {
        doRemoveDependencies(null, null);
    }

    public void removeDependencies(String group) {
        doRemoveDependencies(group, null);
    }

    public void removeDependency(Object dependencyNotation) {
        Dependency dependency = dependencyFactory.createDependency(dependencyNotation);
        doRemoveDependencies(dependency.getGroup(), dependency.getName());
    }

    private void doRemoveDependencies(@Nullable String group, @Nullable String name) {
        for (Iterator<? extends DependencyMetadata> dependenciesIterator = metadata.getDependencies().iterator(); dependenciesIterator.hasNext();) {
            ModuleVersionSelector requested = dependenciesIterator.next().getRequested();
            if (nullOrEquals(group, requested.getGroup()) && nullOrEquals(name, requested.getName())) {
                dependenciesIterator.remove();
                dependencies = null;
            }
        }
    }

    public void addDependency(String configurationName, Object dependencyNotation) {
        addDependency(configurationName, dependencyNotation, null);
    }

    public void addDependency(String configurationName, Object dependencyNotation, @Nullable Closure configureClosure) {
        ClientModule moduleDependency = dependencyFactory.createModule(dependencyNotation, configureClosure);
        DependencyMetadata dependency = dependencyDescriptorFactory.createDependencyDescriptor(configurationName, ImmutableAttributes.EMPTY, moduleDependency);
        metadata.getDependencies().add(dependency);
    }

    private boolean nullOrEquals(String toMatch, String other) {
        return toMatch == null || toMatch.equals(other);
    }
}

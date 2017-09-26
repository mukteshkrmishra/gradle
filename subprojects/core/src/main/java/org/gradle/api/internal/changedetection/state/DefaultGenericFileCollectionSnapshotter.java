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

package org.gradle.api.internal.changedetection.state;

import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.cache.StringInterner;
import org.gradle.api.internal.file.collections.DirectoryFileTreeFactory;
import org.gradle.api.tasks.GenericPropertySnapshotter;
import org.gradle.api.tasks.PropertySnapshotter;
import org.gradle.normalization.internal.InputNormalizationStrategy;

public class DefaultGenericFileCollectionSnapshotter extends AbstractFileCollectionSnapshotter implements GenericFileCollectionSnapshotter {
    public DefaultGenericFileCollectionSnapshotter(StringInterner stringInterner, DirectoryFileTreeFactory directoryFileTreeFactory, FileSystemSnapshotter fileSystemSnapshotter) {
        super(stringInterner, directoryFileTreeFactory, fileSystemSnapshotter);
    }

    @Override
    public Class<? extends PropertySnapshotter> getRegisteredType() {
        return GenericPropertySnapshotter.class;
    }

    @Override
    public FileCollectionSnapshot snapshot(FileCollection files, PathNormalizationStrategy pathNormalizationStrategy, InputNormalizationStrategy inputNormalizationStrategy) {
        return super.snapshot(files, new FileCollectionVisitingSnapshotBuilder(new CollectingFileCollectionSnapshotBuilder(TaskFilePropertyCompareStrategy.UNORDERED, pathNormalizationStrategy, getStringInterner())));
    }
}

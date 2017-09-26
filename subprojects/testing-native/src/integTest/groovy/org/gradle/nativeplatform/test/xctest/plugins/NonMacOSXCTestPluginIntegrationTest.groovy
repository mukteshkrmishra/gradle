/*
 * Copyright 2017 the original author or authors.
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

package org.gradle.nativeplatform.test.xctest.plugins

import org.gradle.integtests.fixtures.AbstractIntegrationSpec
import org.gradle.util.Requires
import org.gradle.util.TestPrecondition

@Requires(TestPrecondition.NOT_MAC_OS_X)
class NonMacOSXCTestPluginIntegrationTest extends AbstractIntegrationSpec {
    def setup() {
        buildFile << "apply plugin: 'xctest'"
    }

    def "logs a warning to the output when applying the plugin under OS other than macOS"() {
        succeeds "tasks"

        expect:
        result.assertOutputContains("XCTest integration cannot be used because 'xctest' plugin was applied under unsupported condition.")
    }

    def "supports the 'test' lifecycle task when xctest plugin cannot be used"() {
        succeeds "test"

        expect:
        result.assertTasksExecuted(":test")
        result.assertTasksSkipped(":test")
        result.assertOutputContains("XCTest integration cannot be used because 'xctest' plugin was applied under unsupported condition.")
    }

    def "supports the 'check' lifecycle task when xctest plugin cannot be used"() {
        succeeds "check"

        expect:
        result.assertTasksExecuted(":test", ":check")
        result.assertTasksSkipped(":test", ":check")
        result.assertOutputContains("XCTest integration cannot be used because 'xctest' plugin was applied under unsupported condition.")
    }
}

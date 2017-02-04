/**
 * openfasttrack-gradle - Gradle plugin for tracing requirements using OpenFastTrack
 * Copyright (C) 2017 Hamster community <christoph at users.sourceforge.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package openfasttrack.gradle;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.Test;

public class OpenFastTrackPluginTest
{
    private static final Path EXAMPLES_DIR = Paths.get("example-projects").toAbsolutePath();
    private static final Path PROJECT_DEFAULT_CONFIG_DIR = EXAMPLES_DIR.resolve("default-config");
    private static final Path PROJECT_CUSTOM_CONFIG_DIR = EXAMPLES_DIR.resolve("custom-config");
    private BuildResult buildResult;

    @Test
    public void testTracingTaskAddedToProject()
    {
        runBuild(PROJECT_DEFAULT_CONFIG_DIR, "tasks", "--stacktrace");
        assertThat(buildResult.getOutput(), containsString(
                "traceRequirements - Trace requirements and generate tracing report"));
    }

    @Test
    public void testTraceExampleProjectWithDefaultConfig() throws IOException
    {
        runBuild(PROJECT_DEFAULT_CONFIG_DIR, "traceRequirements", "--stacktrace");
        assertEquals(buildResult.task(":traceRequirements").getOutcome(), TaskOutcome.SUCCESS);
        assertThat(fileContent(PROJECT_DEFAULT_CONFIG_DIR.resolve("build/reports/tracing.txt")),
                containsString("ok - 0 total"));
    }

    @Test
    public void testTraceExampleProjectWithCustomConfig() throws IOException
    {
        runBuild(PROJECT_CUSTOM_CONFIG_DIR, "traceRequirements", "--stacktrace");
        assertEquals(buildResult.task(":traceRequirements").getOutcome(), TaskOutcome.SUCCESS);
        assertThat(fileContent(PROJECT_CUSTOM_CONFIG_DIR.resolve("build/custom-report.txt")),
                containsString("ok - 0 total"));
    }

    private String fileContent(Path file) throws IOException
    {
        final String reportContent = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
        return reportContent;
    }

    private void runBuild(Path projectDir, String... arguments)
    {
        buildResult = GradleRunner.create() //
                .withProjectDir(projectDir.toFile()) //
                .withPluginClasspath() //
                .withArguments(arguments) //
                .forwardOutput() //
                .build();
    }
}
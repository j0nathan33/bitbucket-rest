/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cdancy.bitbucket.rest.features;

import static org.assertj.core.api.Assertions.assertThat;

import com.cdancy.bitbucket.rest.BaseBitbucketApiLiveTest;
import com.cdancy.bitbucket.rest.GeneratedTestContents;
import com.cdancy.bitbucket.rest.domain.commit.CommitPage;

import com.cdancy.bitbucket.rest.domain.tags.Tag;
import com.cdancy.bitbucket.rest.options.CreateTag;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "TagApiLiveTest", singleThreaded = true)
public class TagApiLiveTest extends BaseBitbucketApiLiveTest {
    
    private GeneratedTestContents generatedTestContents;

    String projectKey;
    String repoKey;
    String commitHash;
    String tagName = randomStringLettersOnly();

    @BeforeClass
    public void init() {
        generatedTestContents = initGeneratedTestContents();
        this.projectKey = generatedTestContents.project.key();
        this.repoKey = generatedTestContents.repository.name();
        
        final CommitPage commitPage = api.commitsApi().list(projectKey, repoKey, Boolean.TRUE, 1, 0);
        assertThat(commitPage.values().isEmpty()).isFalse();
        this.commitHash = commitPage.values().get(0).id();
    }
    
    @Test
    public void testCreateTag() {
        CreateTag createTag = CreateTag.create(tagName, commitHash, null);
        Tag tag = api().create(projectKey, repoKey, createTag);
        assertThat(tag).isNotNull();
        assertThat(tag.errors().isEmpty()).isTrue();
        assertThat(tag.id().endsWith(tagName)).isTrue();
        assertThat(commitHash.equalsIgnoreCase(tag.latestCommit())).isTrue();
    }

    @Test (dependsOnMethods = "testCreateTag")
    public void testGetTag() {
        Tag tag = api().get(projectKey, repoKey, tagName);
        assertThat(tag).isNotNull();
        assertThat(tag.errors().isEmpty()).isTrue();
        assertThat(tag.id().endsWith(tagName)).isTrue();
        assertThat(commitHash.equalsIgnoreCase(tag.latestCommit())).isTrue();
    }

    @Test
    public void testGetTagNonExistent() {
        Tag tag = api().get(projectKey, repoKey, tagName + "9999");
        assertThat(tag).isNull();
    }
    
    @AfterClass
    public void fin() {
        terminateGeneratedTestContents(generatedTestContents);
    }

    private TagApi api() {
        return api.tagApi();
    }
}

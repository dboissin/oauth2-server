/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package jp.eisbahn.oauth2.server.models;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

public class AccessTokenTest {

	@Test
	public void testAuthIdProperty() throws Exception {
		AccessToken target = new AccessToken();
		target.setAuthId("authId1");
		assertEquals("authId1", target.getAuthId());
	}

	@Test
	public void testTokenProperty() throws Exception {
		AccessToken target = new AccessToken();
		target.setToken("token1");
		assertEquals("token1", target.getToken());
	}

	@Test
	public void testExpiresInProperty() throws Exception {
		AccessToken target = new AccessToken();
		target.setExpiresIn(12345L);
		assertEquals(12345L, target.getExpiresIn());
	}

	@Test
	public void testCreatedOnProperty() throws Exception {
		AccessToken target = new AccessToken();
		Date now = new Date();
		target.setCreatedOn(now);
		assertEquals(now, target.getCreatedOn());
	}

}

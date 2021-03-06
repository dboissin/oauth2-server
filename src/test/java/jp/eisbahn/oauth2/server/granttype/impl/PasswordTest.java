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

package jp.eisbahn.oauth2.server.granttype.impl;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import jp.eisbahn.oauth2.server.async.Handler;
import jp.eisbahn.oauth2.server.data.DataHandlerSync;
import jp.eisbahn.oauth2.server.exceptions.Try;
import jp.eisbahn.oauth2.server.mock.MockDataHandler;
import org.apache.commons.codec.binary.Base64;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jp.eisbahn.oauth2.server.data.DataHandler;
import jp.eisbahn.oauth2.server.exceptions.OAuthError;
import jp.eisbahn.oauth2.server.fetcher.clientcredential.ClientCredentialFetcherImpl;
import jp.eisbahn.oauth2.server.granttype.GrantHandler.GrantHandlerResult;
import jp.eisbahn.oauth2.server.models.AccessToken;
import jp.eisbahn.oauth2.server.models.AuthInfo;
import jp.eisbahn.oauth2.server.models.Request;

import java.io.UnsupportedEncodingException;

public class PasswordTest {

	private Password target;

	@Before
	public void setUp() {
		target = new Password();
		target.setClientCredentialFetcher(new ClientCredentialFetcherImpl());
	}

	@After
	public void tearDown() {
		target = null;
	}

	@Test
	public void testHandleRequestUsernameNotFound() throws Exception {
		Request request = createRequestMock();
		expect(request.getParameter("username")).andReturn(null);
		DataHandler dataHandler = createDataHandlerMock(request);
		replay(request, dataHandler);
		target.handleRequest(dataHandler, new Handler<Try<OAuthError, GrantHandlerResult>>() {
			@Override
			public void handle(Try<OAuthError, GrantHandlerResult> event) {
				try {
					event.get();
					fail("Error.InvalidRequest not occurred.");
				} catch (OAuthError e) {
					assertEquals("'username' not found", e.getDescription());
				}
			}
		});
	}

	@Test
	public void testHandleRequestPasswordNotFound() throws Exception {
		Request request = createRequestMock();
		expect(request.getParameter("username")).andReturn("username1");
		expect(request.getParameter("password")).andReturn(null);
		DataHandler dataHandler = createDataHandlerMock(request);
		replay(request, dataHandler);
		target.handleRequest(dataHandler, new Handler<Try<OAuthError, GrantHandlerResult>>() {
			@Override
			public void handle(Try<OAuthError, GrantHandlerResult> event) {
				try {
					event.get();
					fail("Error.InvalidRequest not occurred.");
				} catch (OAuthError e) {
					assertEquals("'password' not found", e.getDescription());
				}
			}
		});
	}

	@Test
	public void testHandleRequestUserIdNotFound() throws Exception {
		Request request = createRequestMock();
		expect(request.getParameter("username")).andReturn("userNotFound");
		expect(request.getParameter("password")).andReturn("password1");
		DataHandlerSync dataHandler = new MockDataHandler(request);
		replay(request);
		target.handleRequest(dataHandler, new Handler<Try<OAuthError, GrantHandlerResult>>() {
			@Override
			public void handle(Try<OAuthError, GrantHandlerResult> event) {
				try {
					event.get();
					fail("Error.InvalidGrant not occurred.");
				} catch (OAuthError e) {
				}
			}
		});
	}

	@Test
	public void testHandleRequestAuthInfoNotFound() throws Exception {
		Request request = createRequestMock();
		expect(request.getParameter("username")).andReturn("authInfoNotFound");
		expect(request.getParameter("password")).andReturn("password1");
		expect(request.getParameter("scope")).andReturn("scope1");
		DataHandlerSync dataHandler = new MockDataHandler(request);
//		expect(dataHandler.getUserId("username1", "password1")).andReturn("userId1");
//		expect(dataHandler.createOrUpdateAuthInfo("clientId1", "userId1", "scope1")).andReturn(null);
		replay(request);
		target.handleRequest(dataHandler, new Handler<Try<OAuthError, GrantHandlerResult>>() {
			@Override
			public void handle(Try<OAuthError, GrantHandlerResult> event) {
				try {
					event.get();
					fail("Error.InvalidGrant not occurred.");
				} catch (OAuthError e) {
				}
			}
		});
	}

	@Test
	public void testHandleRequestClientIdMismatch() throws Exception {
		Request request = createRequestMock();
		expect(request.getParameter("username")).andReturn("clientFailed");
		expect(request.getParameter("password")).andReturn("password1");
		expect(request.getParameter("scope")).andReturn("scope1");
		DataHandlerSync dataHandler = new MockDataHandler(request);
		replay(request);
		target.handleRequest(dataHandler, new Handler<Try<OAuthError, GrantHandlerResult>>() {
			@Override
			public void handle(Try<OAuthError, GrantHandlerResult> event) {
				try {
					event.get();
					fail("Error.InvalidClient not occurred.");
				} catch (OAuthError e) {
				}
			}
		});
	}

	@Test
	public void testHandleRequestSimple() throws Exception {
		Request request = createRequestMock();
		expect(request.getParameter("username")).andReturn("username1");
		expect(request.getParameter("password")).andReturn("password1");
		expect(request.getParameter("scope")).andReturn("scope1");
		DataHandlerSync dataHandler = new MockDataHandler(request);
		replay(request);
		target.handleRequest(dataHandler, new Handler<Try<OAuthError, GrantHandlerResult>>() {

			@Override
			public void handle(Try<OAuthError, GrantHandlerResult> event) {
				try {
					GrantHandlerResult result = event.get();
					assertEquals("Bearer", result.getTokenType());
					assertEquals("accessToken1", result.getAccessToken());
				} catch (OAuthError oAuthError) {
					fail(oAuthError.getMessage());
				}
			}
		});
	}

	@Test
	public void testHandleRequestFull() throws Exception {
		Request request = createRequestMock();
		expect(request.getParameter("username")).andReturn("username1");
		expect(request.getParameter("password")).andReturn("password1");
		expect(request.getParameter("scope")).andReturn("scope1");
		DataHandlerSync dataHandler = new MockDataHandler(request);
		replay(request);
		target.handleRequest(dataHandler, new Handler<Try<OAuthError, GrantHandlerResult>>() {
			@Override
			public void handle(Try<OAuthError, GrantHandlerResult> event) {
				try {
					GrantHandlerResult result = event.get();
					assertEquals("Bearer", result.getTokenType());
					assertEquals("accessToken1", result.getAccessToken());
					assertEquals(900L, (long)result.getExpiresIn());
					assertEquals("refreshToken1", result.getRefreshToken());
					assertEquals("scope1", result.getScope());
				} catch (OAuthError oAuthError) {
					fail(oAuthError.getMessage());
				}
			}
		});
	}

	private Request createRequestMock() {
		Request request = createMock(Request.class);
		try {
			String basic = new String(Base64.encodeBase64("clientId1:clientSecret1".getBytes()), "UTF-8");
			expect(request.getHeader("Authorization")).andReturn("Basic " + basic);
		} catch (UnsupportedEncodingException e) {
			expect(request.getParameter("client_id")).andReturn("clientId1");
			expect(request.getParameter("client_secret")).andReturn("clientSecret1");
		}
		return request;
	}

	private DataHandlerSync createDataHandlerMock(Request request) {
		DataHandlerSync dataHandler = createMock(DataHandlerSync.class);
		expect(dataHandler.getRequest()).andReturn(request);
		return dataHandler;
	}

}

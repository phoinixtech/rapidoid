/*-
 * #%L
 * rapidoid-http-fast
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski and contributors
 * %%
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
 * #L%
 */

package org.rapidoid.http;


import org.junit.Assert;
import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.Buf;
import org.rapidoid.buffer.BufGroup;
import org.rapidoid.data.BufRange;
import org.rapidoid.data.KeyValueRanges;
import org.rapidoid.http.impl.HttpParser;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.net.impl.RapidoidHelper;
import org.rapidoid.test.TestCommons;
import org.rapidoid.u.U;

import java.nio.ByteBuffer;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class HttpParserTest extends TestCommons {

	private static final String CRLF = "\r\n";

	private static String REQ1 = req("GET /foo/bar?a=5&b&n=%20 HTTP/1.1|Host:www.test.com|Set-Cookie: aaa=2||", CRLF);
	private static String REQ2 = req(
		"POST /something/else/here?x=abc%20de HTTP/STRANGE|Host:a.b.c.org|:ign|ored:|My-Header: same|My-Header: again|"
			+ body("a"), CRLF);
	private static String REQ3 = req("PUT /books HTTP/1.0|CoNNectioN: keep-alive | AAAAA: c = 2 |" + body("ab"), CRLF);
	private static String REQ4 = req("DELETE /?a&bb=c&d MY-PROTOCOL|" + body("abc"), CRLF);
	private static String REQ5 = req("ABCD ///??? HTTP/1.1|" + body("abcd"), CRLF);
	private static String REQ6 = req("GET /?x A||", CRLF);

	private static final String CONTENT_LENGTH = "CoNtEnT-LenGth";

	private static String req(String s, String nl) {
		return s.replaceAll("\\|", nl);
	}

	private static String body(String s) {
		String body = "BODY" + s;
		return CONTENT_LENGTH + ": " + body.getBytes().length + "||" + body;
	}

	@Test
	public void shouldParseRequest1() {
		RapidoidHelper req = parse(REQ1);

		BufGroup bufs = new BufGroup(4);
		Buf reqbuf = bufs.from(REQ1, "r2");

		eq(REQ1, req.verb, "GET");
		eq(REQ1, req.path, "/foo/bar");
		eqs(REQ1, req.params, "a", "5", "b", "", "n", "%20");
		eq(req.params.toMap(reqbuf, true, true, false), U.map("a", "5", "b", "", "n", " "));
		eq(REQ1, req.protocol, "HTTP/1.1");
		eqs(REQ1, req.headersKV, "Host", "www.test.com", "Set-Cookie", "aaa=2");

		isNone(req.body);
	}

	@Test
	public void shouldParseRequest2() {
		RapidoidHelper req = parse(REQ2);

		eq(REQ2, req.verb, "POST");
		eq(REQ2, req.path, "/something/else/here");
		eqs(REQ2, req.params, "x", "abc%20de");
		eq(REQ2, req.protocol, "HTTP/STRANGE");
		eqs(REQ2, req.headersKV, "Host", "a.b.c.org", "", "ign", "ored", "", "My-Header", "same", "My-Header", "again",
			CONTENT_LENGTH, "5");
		eq(REQ2, req.query, "x=abc%20de");
		eq(REQ2, req.body, "BODYa");
	}

	@Test
	public void shouldParseRequest3() {
		RapidoidHelper req = parse(REQ3);

		eq(REQ3, req.verb, "PUT");
		eq(REQ3, req.path, "/books");
		eqs(REQ3, req.params);
		eq(REQ3, req.protocol, "HTTP/1.0");
		eqs(REQ3, req.headersKV, "CoNNectioN", "keep-alive", "AAAAA", "c = 2", CONTENT_LENGTH, "6");
		eq(REQ3, req.body, "BODYab");
	}

	@Test
	public void shouldParseRequest4() {
		RapidoidHelper req = parse(REQ4);

		eq(REQ4, req.verb, "DELETE");
		eq(REQ4, req.path, "/");
		eqs(REQ4, req.params, "a", "", "bb", "c", "d", "");
		eq(REQ4, req.protocol, "MY-PROTOCOL");
		eqs(REQ4, req.headersKV, CONTENT_LENGTH, "7");
		eq(REQ4, req.body, "BODYabc");
	}

	@Test
	public void shouldParseRequest5() {
		RapidoidHelper req = parse(REQ5);

		eq(REQ5, req.verb, "ABCD");
		eq(REQ5, req.path, "///");
		eqs(REQ5, req.params, "??", "");
		eq(req.params.toMap(REQ5), U.map("??", ""));
		eq(REQ5, req.protocol, "HTTP/1.1");
		eqs(REQ5, req.headersKV, CONTENT_LENGTH, "8");
		eq(REQ5, req.body, "BODYabcd");
	}

	@Test
	public void shouldParseRequest6() {
		RapidoidHelper req = parse(REQ6);

		eq(REQ6, req.verb, "GET");
		eq(REQ6, req.path, "/");
		eqs(REQ6, req.params, "x", "");
		eq(REQ6, req.protocol, "A");
		eqs(REQ6, req.headersKV);
		isNone(req.body);
	}

	private RapidoidHelper parse(String reqs) {
		RapidoidHelper req = new RapidoidHelper();

		Buf reqbuf = new BufGroup(1024).from(reqs, "test");

		Channel conn = mock(Channel.class);
		returns(conn.input(), reqbuf);
		returns(conn.helper(), req);

		HttpParser parser = new HttpParser();
		parser.parse(reqbuf, req);

		parser.parseParams(reqbuf, req.params, req.query);

		parser.parseHeadersIntoKV(reqbuf, req.headers, req.headersKV, req.cookies, req);

		return req;
	}

	protected void eq(String whole, BufRange range, String expected) {
		eq(range.get(whole), expected);
	}

	protected void eqs(String whole, KeyValueRanges ranges, String... keysAndValues) {
		eq(keysAndValues.length % 2, 0);
		eq(ranges.count, keysAndValues.length / 2);
		for (int i = 0; i < ranges.count; i++) {
			BufRange key = ranges.keys[i];
			BufRange value = ranges.values[i];
			eq(whole, key, keysAndValues[i * 2]);
			eq(whole, value, keysAndValues[i * 2 + 1]);
		}
	}

	protected void eq(BufRange range, int start, int length) {
		Assert.assertEquals(range.start, start);
		Assert.assertEquals(range.length, length);
	}

	protected void isNone(BufRange range) {
		Assert.assertEquals(range.start, -1);
		Assert.assertEquals(range.length, 0);
	}

	protected void eq(Buf buf, String expected) {
		eq(buf.size(), expected.getBytes().length);
		eq(buf.data(), expected);

		byte[] bbytes = new byte[buf.size()];
		ByteBuffer bufy = ByteBuffer.wrap(bbytes);
		buf.writeTo(bufy);
		eq(new String(bbytes), expected);

		int size = (int) Math.ceil(expected.length() * 1.0 / buf.unitSize());
		isTrue(buf.unitCount() == size || buf.unitCount() == size + 1);

		byte[] bytes = expected.getBytes();
		synchronized (buf) {
			for (int i = 0; i < bytes.length; i++) {
				eq((char) buf.get(i), (char) bytes[i]);
			}
		}

		for (int len = 2; len < 10; len++) {
			for (int p = 0; p <= buf.size() - len; p++) {
				String sub = buf.get(new BufRange(p, len));
				eq(sub, expected.substring(p, p + len));
			}
		}
	}

}

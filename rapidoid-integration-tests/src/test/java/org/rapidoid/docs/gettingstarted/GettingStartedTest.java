/*-
 * #%L
 * rapidoid-integration-tests
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

package org.rapidoid.docs.gettingstarted;


import org.rapidoid.annotation.IntegrationTest;
import org.rapidoid.docs.DocTest;
import org.rapidoid.test.Doc;
import org.rapidoid.u.U;

@IntegrationTest(main = Main.class)
@Doc(title = "RESTful services with Lambda handlers, JPA, Jackson and Bean Validation")
public class GettingStartedTest extends DocTest {

	@Override
	protected void exercise() {
		POST("/books", U.map("title", "Java Book", "year", 2016));

		GET("/books");

		POST("/books", U.map("year", 2004));

		GET("/books");
	}

}

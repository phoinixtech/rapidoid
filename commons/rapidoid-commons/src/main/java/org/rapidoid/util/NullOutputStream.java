/*-
 * #%L
 * rapidoid-commons
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

package org.rapidoid.util;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.io.IOException;
import java.io.OutputStream;


@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class NullOutputStream extends OutputStream {

	@Override
	public void write(int b) {
	}

	@Override
	public void write(byte[] b, int off, int len) {
	}

	@Override
	public void write(byte[] b) throws IOException {
	}

	@Override
	public void flush() throws IOException {
	}

	@Override
	public void close() throws IOException {
	}

}

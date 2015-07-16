package org.rapidoid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.rapidoid.util.U;

/*
 * #%L
 * rapidoid-insights
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

/**
 * @author Nikolche Mihajlovski
 * @since 4.1.0
 */
public class Insights {

	private static final Map<String, List<Insightful>> RESOURCES = U.autoExpandingMap(ArrayList.class);

	public static void register(Insightful resource) {
		RESOURCES.get(resource.getKind()).add(resource);
	}

	public static String getInfo() {
		return RESOURCES.toString();
	}

	public static String getCpuMemStats() {
		Runtime rt = Runtime.getRuntime();

		long totalMem = rt.totalMemory();
		long maxMem = rt.maxMemory();
		long freeMem = rt.freeMemory();
		long usedMem = totalMem - freeMem;
		int megs = 1024 * 1024;

		String msg = "MEM [total=%s MB, used=%s MB, max=%s MB]";
		return String.format(msg, totalMem / megs, usedMem / megs, maxMem / megs);
	}

}

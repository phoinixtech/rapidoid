package org.rapidoid.gui.base;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.GUI;
import org.rapidoid.html.Tag;
import org.rapidoid.html.TagWidget;
import org.rapidoid.html.impl.TagRenderer;
import org.rapidoid.html.tag.TdTag;
import org.rapidoid.html.tag.ThTag;
import org.rapidoid.util.Constants;

import java.util.UUID;

/*
 * #%L
 * rapidoid-gui
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public abstract class AbstractWidget<W extends AbstractWidget<?>> extends RapidoidThing implements TagWidget<Object>, Constants {

	private Object extra;

	protected abstract Object render();

	private volatile boolean visible = true;

	@Override
	public final Object render(Object extra) {
		this.extra = extra;
		// TODO ignore the exchange?

		Object rendered = render();

		if (!visible) {
			rendered.toString(); // to process events
			return "";
		}

		return rendered;
	}

	public String widgetId() {
		return getClass().getSimpleName() + "-" + UUID.randomUUID();
	}

	public boolean visible() {
		return visible;
	}

	public W visible(boolean visible) {
		this.visible = visible;
		return (W) this;
	}

	@Override
	public String toString() {
		return TagRenderer.get().toHTML(this, extra);
	}

	protected static Tag div(Object... contents) {
		return GUI.div(contents);
	}

	protected static Tag span(Object... contents) {
		return GUI.span(contents);
	}

	protected static Tag li(Object... contents) {
		return GUI.li(contents);
	}

	protected static Tag tr(Object... contents) {
		return GUI.tr(contents);
	}

	protected static TdTag td(Object... contents) {
		return GUI.td(contents);
	}

	protected static ThTag th(Object... contents) {
		return GUI.th(contents);
	}

}

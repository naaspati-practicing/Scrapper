package sam.manga.scrapper.impl.mangafox;

import static javax.script.ScriptContext.ENGINE_SCOPE;
import static javax.script.ScriptContext.GLOBAL_SCOPE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import sam.manga.scrapper.ScrapperException;

@SuppressWarnings("restriction")
public class JsEngine {
	private static volatile JsEngine jsengine;

	public synchronized static Result parse(String script) throws ScrapperException {
			if(jsengine == null)
				jsengine = new JsEngine();
			try {
				jsengine.eval(script);
				return  new Result(jsengine);
			} finally {
				if(jsengine != null) {
					jsengine.val = null;
					jsengine.imgUrls = null;
					jsengine.urls = null;
				}
			}
	}

	public static class Result {
		public final String val;
		public final String[] imgUrls;
		public final String[] urls;

		Result(JsEngine engine) {
			this.val = engine.val;
			this.imgUrls = engine.imgUrls;
			this.urls = engine.urls;
		}
	}

	String val;
	String[] imgUrls;
	String[] urls;	

	private final ScriptEngine js = new ScriptEngineManager().getEngineByExtension("js");
	final Bindings engine = js.getBindings(ENGINE_SCOPE);
	final Bindings global = js.getBindings(GLOBAL_SCOPE);
	final Set<String> existing;

	@SuppressWarnings("rawtypes")
	private JsEngine() {
		Bindings val_bind = new SimpleBindings();
		val_bind.put("val", (Consumer)(this::setVal));

		Function func = s -> val_bind;

		engine.put("$", func);
		global.put("$", func);

		existing = new HashSet<>();
		existing.addAll(engine.keySet());
		existing.addAll(global.keySet());
	}

	public void eval(String script) throws ScrapperException {
		try {
			clear(engine);
			clear(global);

			js.eval("var sam_evaled = "+script+";");

			urls = get("sam_evaled")
					.filter(s -> s instanceof ScriptObjectMirror)
					.map(s -> (ScriptObjectMirror)s)
					.filter(ScriptObjectMirror::isArray)
					.map(s -> s.to(String[].class))
					.orElse(null);

			imgUrls = get("newImgs", String[].class); // set by mangafox
			if(imgUrls == null) {
				imgUrls = Optional.ofNullable(get("dm5imagefun", Supplier.class))
						.map(Supplier::get)
						.map(s -> map(s, String[].class))
						.orElse(null);  // set by mangahere
			}
		} catch (ScriptException e) {
			throw new ScrapperException(script, e);
		}
	}
	private Optional<Object> get(String key) {
		Object s = engine.get(key);

		if(s != null)
			return Optional.of(s);

		s = global.get(key);

		if(s != null)
			return Optional.of(s);

		return Optional.empty();
	}

	private void clear(Bindings engine) {
		for (String s : new ArrayList<>(engine.keySet())) {
			if(!existing.contains(s)) 
				engine.put(s, null);
		}
	}

	private <E> E get(String key, Class<E> cls) {
		return map(get(key).orElse(null), cls);
	}
	private <E> E map(Object object, Class<E> cls) {
		if(object == null)
			return null;

		return ((ScriptObjectMirror)object).to(cls);
	}

	private void setVal(Object val) {
		this.val = (String)val;
	}

	@Override
	public String toString() {
		return "JsEngine [imgUrls=" + Arrays.toString(imgUrls) + ", urls=" + Arrays.toString(urls) + "]";
	}
}

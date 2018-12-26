package sam.manga.scrapper.impl.mangafox;

import static javax.script.ScriptContext.ENGINE_SCOPE;
import static javax.script.ScriptContext.GLOBAL_SCOPE;

import java.io.Reader;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import sam.reference.WeakQueue;

class JsEngine implements AutoCloseable {
	private final ScriptEngine js = new ScriptEngineManager().getEngineByExtension("js");
	String val;
	String[] imgUrls;
	String[] urls;

	private volatile boolean open;

	@SuppressWarnings("rawtypes")
	private JsEngine() {
		Bindings val_bind = new SimpleBindings();
		val_bind.put("val", (Consumer)(this::setVal));

		js.getBindings(GLOBAL_SCOPE)
		.put("$", (Function)(s -> val_bind));
	}

	public void eval(Reader script) throws ScriptException {
		if(!open)
			throw new IllegalStateException("closed");
		
		_eval(js.eval(script));
	}
	public void eval(String script) throws ScriptException {
		if(!open)
			throw new IllegalStateException("closed");
		
		_eval(js.eval(script));
	}
	private void _eval(Object obj) {
		if(obj != null)
			urls = ((ScriptObjectMirror)obj).to(String[].class);
		else if(js.get("newImgs") != null) 
			imgUrls = ((ScriptObjectMirror)js.get("newImgs")).to(String[].class);
	}
	private void setVal(Object val) {
		this.val = (String)val;
	}

	private  static final WeakQueue<JsEngine> jsEngines = new WeakQueue<>(true, JsEngine::new);
	public static JsEngine get() {
		JsEngine e = jsEngines.poll();
		e.open = true;
		return e;
	}
	
	@Override
	public void close() {
		if(!open)
			return;
		
		open = false;
		val = null;
		imgUrls = null;
		urls = null;
		
		js.getBindings(ENGINE_SCOPE).clear();
		Object s = js.getBindings(GLOBAL_SCOPE).get("$");
		js.getBindings(GLOBAL_SCOPE).clear();
		js.getBindings(GLOBAL_SCOPE).put("$", s);
		
		jsEngines.offer(this);
	}
}

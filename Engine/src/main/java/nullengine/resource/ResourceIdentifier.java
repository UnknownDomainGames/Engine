package nullengine.resource;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import nullengine.util.JsonUtils;

public class ResourceIdentifier {
	private JsonObject jsonObject;
	private ResourceIdentifier(JsonObject object){
		this.jsonObject=object;
		jsonObject.add("resource", new JsonPrimitive(0)); //todo
	}
	public ResourceIdentifier(String name){
		new ResourceIdentifier(JsonUtils.gson().toJson(name));
	}
	public JsonObject getJsonObject(){
		return jsonObject;
	}
}

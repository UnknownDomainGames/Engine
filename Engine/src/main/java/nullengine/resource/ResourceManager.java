package nullengine.resource;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import nullengine.block.BaseBlock;
import nullengine.item.Item;
import nullengine.item.ItemImpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ResourceManager {
	public static List<JsonElement> getResourceElements(ResourceIdentifier resourceIdentifier){
		List<JsonElement> list=new ArrayList<>();
		JsonObject jsonObject=resourceIdentifier.getJsonObject();
		Iterator<JsonElement> iterator=jsonObject.getAsJsonArray("").iterator();
		while(iterator.hasNext())
			list.add(iterator.next());
		return list;
	}
	public static Item getItemFromList(JsonObject object) {
		if(!object.isJsonNull()) {
			String itemName = object.get("item_name").toString();
			ItemImpl.ItemBuilder builder = new ItemImpl.ItemBuilder();
			ItemImpl item = builder.setName(itemName).build();
			return item;
		}
		else return null;
	}
	public static BaseBlock getBlockFromList(JsonObject object) {
		if (!object.isJsonNull()) {
			//String blockName=object.get("block_name").toString();
			return new BaseBlock();
		} else
			return null;
	}
}
package nullengine.resmanage;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import nullengine.item.Item;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ResourceManager {
	public List<JsonElement> getResourceElements(ResourceIdentifier resourceIdentifier){
		List<JsonElement> list=new ArrayList<>();
		JsonObject jsonObject=resourceIdentifier.getJsonObject();
		Iterator<JsonElement> iterator=jsonObject.getAsJsonArray("").iterator();
		while(iterator.hasNext())
			list.add(iterator.next());
		return list;
	}
	public List<Item> getItemFromList(List<JsonElement> list){
		List<Item> itemList=new ArrayList<>();
		Iterator iterator=list.iterator();
		while(iterator.hasNext()){
			JsonElement element=(JsonElement)iterator.next();
			if(element.toString().equals("item")){

			}
		}
		return itemList;
	}
}

package nullengine.event.world.item;

import nullengine.entity.Entity;
import nullengine.event.Event;
import nullengine.item.Item;
import nullengine.world.World;

public class ItemBreakEvent implements Event {
	private World world;
	private Entity player;
	private Item item;
	public ItemBreakEvent(Item item,World world, Entity player){
		this.world=world;
		this.item=item;
		this.player=player;
	}

	@Override
	public boolean isCancellable() {
		return true;
	}

	public Entity getPlayer() {
		return player;
	}

	public World getWorld() {
		return world;
	}

	public Item getItem() {
		return item;
	}
}

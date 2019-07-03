package nullengine.event.world.item;

import nullengine.block.Block;
import nullengine.entity.Entity;
import nullengine.entity.item.ItemEntity;
import nullengine.event.Cancellable;
import nullengine.event.Event;
import nullengine.item.Item;
import nullengine.world.World;

public class ItemThrowAwayEvent implements Event, Cancellable {
	private World world;
	private Entity player;
	private Item item;
	private Block block;
	public ItemThrowAwayEvent(Item item,World world, Entity player, ItemEntity itemEntity){
		this.world=world;
		this.item=item;
		this.player=player;
	}

	@Override
	public boolean isCancellable() {
		return true;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public void setCancelled(boolean cancelled) {

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

	public Block getBlock() {
		return block;
	}
}
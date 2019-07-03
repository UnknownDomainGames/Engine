package nullengine.event.world.entity;

import nullengine.entity.Entity;
import nullengine.event.Cancellable;
import nullengine.item.BaseItem;
import nullengine.world.World;

public class EntityHitEvent extends EntityEvent implements Cancellable {
	public World world;
	public Entity entitySource;
	public Entity thisEntity;
	public BaseItem itemDamageSource;

	public EntityHitEvent(World world,Entity entitySource,Entity thisEntity,BaseItem itemDamageSource){
		super(thisEntity);
		this.world=world;
		this.entitySource=entitySource;
		this.thisEntity=thisEntity;
		this.itemDamageSource=itemDamageSource;
	}

	public World getWorld() {
		return world;
	}

	public Entity getEntitySource() {
		return entitySource;
	}

	public Entity getEntity() {
		return thisEntity;
	}

	public BaseItem getItemDamageSource() {
		return itemDamageSource;
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
}

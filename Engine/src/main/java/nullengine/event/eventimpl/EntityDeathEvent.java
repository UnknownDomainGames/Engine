package nullengine.event.eventimpl;
import nullengine.entity.Entity;
import nullengine.event.world.entity.EntityEvent;

public class EntityDeathEvent<T> extends EntityEvent {
	public T damageSource;
	public Entity damageEntitySource;
	public Entity deadEntity;
	public EntityDeathEvent(T damageSource,Entity damageEntitySource,Entity deadEntity){
		super(deadEntity);
		this.damageEntitySource=damageEntitySource;
		this.deadEntity=deadEntity;
		this.damageSource=damageSource;
	}
}

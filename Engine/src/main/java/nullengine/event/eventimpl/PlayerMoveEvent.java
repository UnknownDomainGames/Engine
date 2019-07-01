package nullengine.event.eventimpl;

import nullengine.entity.PlayerEntity;
import nullengine.event.world.entity.EntityEvent;
import nullengine.math.BlockPos;

import javax.annotation.Nullable;

public class PlayerMoveEvent extends EntityEvent {
	protected PlayerEntity playerEntity;
	protected BlockPos curPos;
	protected BlockPos lastPos;

	public PlayerMoveEvent(PlayerEntity playerEntity,BlockPos curPos,@Nullable BlockPos lastPos){
		super(playerEntity);
		this.playerEntity=playerEntity;
		this.curPos=curPos;
		this.lastPos=lastPos;
	}
}

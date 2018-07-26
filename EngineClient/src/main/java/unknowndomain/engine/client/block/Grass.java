package unknowndomain.engine.client.block;

import unknowndomain.engine.api.block.Block;
import unknowndomain.engine.api.math.BlockPos;
import unknowndomain.engine.api.math.BoundingBox;
import unknowndomain.engine.api.math.Vector3d;
import unknowndomain.engine.api.util.DomainedPath;
import unknowndomain.engine.api.world.World;
import unknowndomain.engine.client.block.model.BasicData;
import unknowndomain.engine.client.block.model.GameItem;
import unknowndomain.engine.client.block.model.Mesh;
import unknowndomain.engine.client.block.model.Texture;

public class Grass implements Block{
	private BoundingBox box;
	private World world;
	private BlockPos blockPos;
	private Texture texture;
	private final GameItem gameItem;
	
	public Grass(World world,BlockPos blockPos) {
		box=new BoundingBox() {
			@Override
			public Vector3d getCentre() {
				return null;
			}

			@Override
			public boolean isCollided(BoundingBox others) {
				// TODO Auto-generated method stub
				return false;
			}
		};
		this.world=world;
		this.blockPos=blockPos;
		try {
			setTexture(new Texture("/textures/grassblock.png"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Mesh mesh = new Mesh(BasicData.INSTANCE.getPositions(), BasicData.INSTANCE.getTextCoords()
				, BasicData.INSTANCE.getIndices(), texture);
		gameItem=new GameItem(mesh);
		gameItem.setPosition(blockPos);
	}
	
	@Override
	public DomainedPath getRegistryName() {
		return null;
	}

	@Override
	public Block setRegistryName(DomainedPath location) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<Block> getRegistryType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BoundingBox getBoundingBox() {
		return box;
	}

	@Override
	public boolean onBlockPlaced(BlockPos pos) {
		return false;
	}

	@Override
	public boolean onBlockDestroyed(BlockPos pos, boolean harvested) {
		return false;
	}

	@Override
	public BlockPos getBlockPos() {
		return this.blockPos;
	}

	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	public World getWorld() {
		return world;
	}

	public GameItem getGameItem() {
		return this.gameItem;
	}
}

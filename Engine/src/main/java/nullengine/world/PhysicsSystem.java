package nullengine.world;

import nullengine.block.Block;
import nullengine.entity.Entity;
import nullengine.event.Listener;
import nullengine.event.world.WorldTickEvent;
import nullengine.math.AABBs;
import nullengine.math.BlockPos;
import org.joml.AABBd;
import org.joml.Vector3f;

import java.util.List;

public class PhysicsSystem {
    @Listener
    public void tick(WorldTickEvent event) {
        World world = event.getWorld();
        List<Entity> entityList = world.getEntities();
        for (Entity entity : entityList) {
            Vector3f motion = entity.getMotion();
            if (motion.x == 0 && motion.y == 0 && motion.z == 0)
                continue;
            Vector3f direction = new Vector3f(motion);
            Vector3f position = entity.getPosition();
            AABBd box = entity.getBoundingBox();
            if (box == null)
                continue;

            BlockPos localPos = BlockPos.of(((int) Math.floor(position.x)), ((int) Math.floor(position.y)),
                    ((int) Math.floor(position.z)));

//                 int directionX = motion.x == -0 ? 0 : Float.compare(motion.x, 0),
//                 directionY = motion.y == -0 ? 0 : Float.compare(motion.y, 0),
//                 directionZ = motion.z == -0 ? 0 : Float.compare(motion.z, 0);

            AABBd entityBox = AABBs.translate(box, position.add(direction, new Vector3f()), new AABBd());
            List<BlockPos>[] around = AABBs.around(entityBox, motion);
            for (List<BlockPos> ls : around) {
                ls.add(localPos);
            }
            List<BlockPos> faceX = around[0], faceY = around[1], faceZ = around[2];

            double xFix = Integer.MAX_VALUE, yFix = Integer.MAX_VALUE, zFix = Integer.MAX_VALUE;
            if (faceX.size() != 0) {
                for (BlockPos pos : faceX) {
                    Block block = world.getBlock(pos);
                    AABBd[] blockBoxes = block.getShape().getBoundingBoxes(world, pos, block);
                    if (blockBoxes.length != 0)
                        for (AABBd blockBoxLocal : blockBoxes) {
                            AABBd blockBox = AABBs.translate(blockBoxLocal,
                                    new Vector3f(pos.getX(), pos.getY(), pos.getZ()), new AABBd());
                            if (blockBox.testAABB(entityBox)) {
                                xFix = Math.min(xFix, Math.min(Math.abs(blockBox.maxX - entityBox.minX),
                                        Math.abs(blockBox.minX - entityBox.maxX)));
                            }
                        }
                }
            }
            if (faceY.size() != 0) {
                for (BlockPos pos : faceY) {
                    Block block = world.getBlock(pos);
                    AABBd[] blockBoxes = block.getShape()
                            .getBoundingBoxes(world, pos, block);
                    if (blockBoxes.length != 0)
                        for (AABBd blockBox : blockBoxes) {
                            AABBd translated = AABBs.translate(blockBox,
                                    new Vector3f(pos.getX(), pos.getY(), pos.getZ()), new AABBd());
                            if (translated.testAABB(entityBox)) {
                                yFix = Math.min(yFix, Math.min(Math.abs(translated.maxY - entityBox.minY),
                                        Math.abs(translated.minY - entityBox.maxY)));
                            }
                        }
                }
            }
            if (faceZ.size() != 0) {
                for (BlockPos pos : faceZ) {
                    Block block = world.getBlock(pos);
                    AABBd[] blockBoxes = block.getShape()
                            .getBoundingBoxes(world, pos, block);
                    if (blockBoxes.length != 0)
                        for (AABBd blockBox : blockBoxes) {
                            AABBd translated = AABBs.translate(blockBox,
                                    new Vector3f(pos.getX(), pos.getY(), pos.getZ()), new AABBd());
                            if (translated.testAABB(entityBox)) {
                                zFix = Math.min(zFix, Math.min(Math.abs(translated.maxZ - entityBox.minZ),
                                        Math.abs(translated.minZ - entityBox.maxZ)));
                            }
                        }
                }
            }
            if (Integer.MAX_VALUE != xFix)
                motion.x = 0;
            if (Integer.MAX_VALUE != yFix)
                motion.y = 0;
            if (Integer.MAX_VALUE != zFix) {
                motion.z = 0;
            }

            // if (motion.y > 0) motion.y -= 0.01f;
            // else if (motion.y < 0) motion.y += 0.01f;
            // if (Math.abs(motion.y) <= 0.01f) motion.y = 0; // physics update
        }
    }
}

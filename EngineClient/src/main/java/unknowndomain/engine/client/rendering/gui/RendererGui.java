package unknowndomain.engine.client.rendering.gui;

import org.joml.AABBd;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import unknowndomain.engine.block.BlockPrototype;
import unknowndomain.engine.client.UnknownDomain;
import unknowndomain.engine.client.shader.RendererShaderProgram;
import unknowndomain.engine.client.shader.Shader;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.math.AABBs;
import unknowndomain.engine.world.World;

import java.nio.ByteBuffer;
import java.text.DecimalFormat;

/**
 * render for any gui
 */
public class RendererGui extends RendererShaderProgram {
    private TTFFontRenderer fontRenderer;

    public RendererGui(ByteBuffer fontRenderer, Shader vertexShader, Shader fragShader) {
        this.fontRenderer = new TTFFontRenderer(fontRenderer);
        createShader(vertexShader, fragShader);
    }


    protected void createShader(Shader vertexShader, Shader fragShader) {
        programId = GL20.glCreateProgram();

        attachShader(vertexShader);
        attachShader(fragShader);

        linkProgram();
        useProgram();

        vertexShader.deleteShader();
        fragShader.deleteShader();


        GL20.glValidateProgram(programId);

        setUniform("projection", new Matrix4f().setOrtho(0, 854f, 480f, 0, 1, -1));
        setUniform("model", new Matrix4f().identity());

        GL20.glUseProgram(0);

        Tessellator.getInstance().setShaderId(programId);
    }

    @Override
    protected void useProgram() {
        super.useProgram();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void render(Context context) {
        useProgram();
        setUniform("usingAlpha", true);

        debug(context);
    }

    private long lastFPS = getTime();
    private int fps = 0;
    private int displayFPS = 0;

    public long getTime() {
        return System.nanoTime() / 1000000;
    }

    public void updateFPS() {
        long time = getTime();
        if (time - lastFPS > 1000) {
            displayFPS = fps;
            fps = 0; //reset the FPS counter
            lastFPS += 1000; //add one second
        }
        fps++;
    }

    void debug(Context context) {
        updateFPS();
        Entity player = UnknownDomain.getGame().getPlayer().getMountingEntity();
        AABBd box = AABBs.translate(player.getBoundingBox(), player.getPosition(), new AABBd());
        World world = UnknownDomain.getGame().getPlayer().getWorld();

        BlockPrototype.Hit hit = world.raycast(context.getCamera().getPosition(),
                context.getCamera().getFrontVector(), 5);
        fontRenderer.drawText("fps " + displayFPS, 0, 0, 0xffffffff, 16);
        fontRenderer.drawText(String.format("Playerlocation: %f, %f, %f", player.getPosition().x, player.getPosition().y, player.getPosition().z), 0, 25, 0xffffffff, 16);
        fontRenderer.drawText(String.format("Player bounding box: %s", box.toString(new DecimalFormat("#.##"))), 0, 45, 0xffffffff, 16);
        fontRenderer.drawText(player.getBehavior(Entity.TwoHands.class).getMainHand().getLocalName(), 0, 64, 0xffffffff, 16);

        if (hit != null) {

            Vector3f hitedPos = new Vector3f(hit.position.getX(), hit.position.getY(), hit.position.getZ());
            AABBd blockAABB = AABBs.translate(hit.block.getBoundingBoxes()[0], hitedPos, new AABBd());
            fontRenderer.drawText(String.format("Looking at: %f,%f,%f", hitedPos.x, hitedPos.y, hitedPos.z), 0, 85, 0xffffffff, 16);
            fontRenderer.drawText(String.format("bounding box: %s", blockAABB.toString(new DecimalFormat("#.##"))), 0, 105, 0xffffffff, 16);
            fontRenderer.drawText(String.format("Collided with the looking box: %s", blockAABB.testAABB(box)), 0, 125, 0xffffffff, 16);

            //fontRenderer.drawText(String.format("[%f, %f, %f, %f, %f, %f]", box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ), 0, 0, 0xffffffff, 32);
            //fontRenderer.drawText(String.format("[%f, %f, %f, %f, %f, %f]", blockAABB.minX, blockAABB.minY, blockAABB.minZ, blockAABB.maxX, blockAABB.maxY, blockAABB.maxZ), 0, 35, 0xffffffff, 32);
            //fontRenderer.drawText(String.format("%s", blockAABB.testAABB(box) ? "coll" : "fine"), 0, 69, 0xffffffff, 32);
        }
    }

    @Override
    public void dispose() {

    }
}

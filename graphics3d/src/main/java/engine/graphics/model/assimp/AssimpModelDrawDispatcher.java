package engine.graphics.model.assimp;

import engine.graphics.Scene3D;
import engine.graphics.graph.DrawDispatcher;
import engine.graphics.graph.Drawer;
import engine.graphics.graph.FrameContext;
import engine.graphics.graph.Renderer;
import engine.graphics.internal.graph.ViewportOpaqueDrawDispatcher;
import engine.graphics.light.LightManager;
import engine.graphics.shader.ShaderResource;
import engine.graphics.shader.UniformBlock;
import engine.graphics.shader.UniformTexture;
import engine.graphics.viewport.Viewport;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;

public class AssimpModelDrawDispatcher implements DrawDispatcher {
    private final Viewport viewport;
    private UniformTexture uniformDiffuseUV;
    private UniformTexture uniformSpecularUV;
    private UniformTexture uniformNormalUV;
    private UniformTexture uniformAlphaUV;
    private UniformBlock uniformLight;
    private UniformBlock uniformMatrices;
    private UniformBlock uniformBones;
    private UniformBlock uniformMaterial;

    public AssimpModelDrawDispatcher(Viewport viewport) {
        this.viewport = viewport;
    }

    @Override
    public void init(Drawer drawer) {
        ShaderResource resource = drawer.getShaderResource();
        this.uniformMatrices = resource.getUniformBlock("Matrices");
        this.uniformBones = resource.getUniformBlock("Bones");
        this.uniformLight = resource.getUniformBlock("Light");
        this.uniformMaterial = resource.getUniformBlock("Material");
        this.uniformDiffuseUV = resource.getUniformTexture("diffuseUV");
        this.uniformSpecularUV = resource.getUniformTexture("specularUV");
        this.uniformNormalUV = resource.getUniformTexture("normalUV");
        this.uniformAlphaUV = resource.getUniformTexture("alphaUV");
//      this.uniformTexture = resource.getUniformTexture("u_Texture");
    }

    @Override
    public void draw(FrameContext frameContext, Drawer drawer, Renderer renderer) {
        Scene3D scene = viewport.getScene();
        ShaderResource resource = drawer.getShaderResource();
        FrustumIntersection frustum = viewport.getFrustum();
        LightManager lightManager = scene.getLightManager();
        lightManager.setup(viewport.getCamera());
        uniformLight.set(lightManager);
        scene.getRenderQueue().getGeometryList(AssimpMesh.ASSIMP_MODEL).stream()
                .filter(geometry -> geometry != null && geometry.getBoundingVolume().test(frustum))
                .forEach(geometry -> {
                    uniformMatrices.set(new ViewportOpaqueDrawDispatcher.Matrices( // TODO: optimize it
                            viewport.getProjectionMatrix(),
                            viewport.getViewMatrix(),
                            geometry.getWorldTransform().getTransformMatrix(new Matrix4f())));
                    if (geometry instanceof AssimpMesh) {
                        uniformBones.set(new AssimpAnimation.StructBones(((AssimpMesh) geometry).getMeshParent().getCurrentAnimation().getCurrentFrame().getJointMatrices()));
                    }
                    var material = geometry.getMaterial();
                    uniformMaterial.set(material);
                    var u = material.getDiffuseMap();
                    if (u != null) {
                        uniformDiffuseUV.set(u);
                    }
                    if ((u = material.getSpecularMap()) != null) {
                        uniformSpecularUV.set(u);
                    }
                    if ((u = material.getNormalMap()) != null) {
                        uniformNormalUV.set(u);
                    }
                    if ((u = material.getAlphaMap()) != null) {
                        uniformAlphaUV.set(u);
                    }
                    resource.refresh();
                    renderer.drawMesh(geometry.getMesh());
                });
    }
}

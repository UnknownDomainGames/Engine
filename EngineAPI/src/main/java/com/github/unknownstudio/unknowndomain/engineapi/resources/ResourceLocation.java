package com.github.unknownstudio.unknowndomain.engineapi.resources;

import com.google.common.base.Strings;

import javax.annotation.Nonnull;

public class ResourceLocation {
    private String domain;
    private String path;

    protected ResourceLocation() {}

    public ResourceLocation(String domain,@Nonnull String path){
        if(Strings.isNullOrEmpty(path))
            throw new IllegalArgumentException();
        this.domain = Strings.nullToEmpty(domain);
        this.path = path;
    }

    public ResourceLocation(@Nonnull String resource){
        if(Strings.isNullOrEmpty(resource))
            throw new IllegalArgumentException();

        String args[] = resource.split(":", 2);
        if(args.length < 2){
            this.domain = "";
            this.path = args[0];
        }else{
            this.domain = args[0];
            this.path = args[1];
        }
    }

    @Nonnull
    public String getDomain(){
        return domain;
    }

    @Nonnull
    public String getPath(){
        return path;
    }

    protected void setDomain(@Nonnull String domain){
        if(domain.isEmpty())
            throw new IllegalArgumentException();
        this.domain = domain;
    }

    protected void setPath(@Nonnull String path){
        if(path.isEmpty())
            throw new IllegalArgumentException();
        this.path = path;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;

        if(!(obj instanceof ResourceLocation))
            return false;

        ResourceLocation resourceLocation = (ResourceLocation) obj;

        if(!resourceLocation.getDomain().equals(getDomain()))
            return false;

        if(!resourceLocation.getPath().equals(getPath()))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return getDomain().hashCode()*31+getPath().hashCode();
    }

    @Override
    public String toString() {
        return Strings.isNullOrEmpty(getDomain())?getPath():getDomain()+":"+getPath();
    }

}

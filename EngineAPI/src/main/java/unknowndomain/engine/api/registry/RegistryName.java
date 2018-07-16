package unknowndomain.engine.api.registry;

import com.google.common.base.Strings;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.Validate;

public class RegistryName {
	
    private String domain;
    private String path;

    protected RegistryName() {}

    public RegistryName(String domain, @Nonnull String path){
        this.domain = Strings.nullToEmpty(domain);
        this.path = Validate.notEmpty(path);;
    }

    public RegistryName(@Nonnull String resource){
    	Validate.notEmpty(resource);

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
        this.domain = Validate.notEmpty(domain);
    }

    protected void setPath(@Nonnull String path){
        this.path = Validate.notEmpty(path);
    }

    @Override
	public int hashCode() {
		return domain.hashCode() * 31 + path.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof RegistryName))
			return false;
		
		RegistryName other = (RegistryName) obj;
		
		if (!domain.equals(other.domain))
			return false;
		if (!path.equals(other.path))
			return false;
		
		return true;
	}

	@Override
    public String toString() {
        return Strings.isNullOrEmpty(getDomain())?getPath():getDomain()+":"+getPath();
    }

}

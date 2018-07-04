package com.github.unknownstudio.unknowndomain.engineapi.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class SimpleResourceLoader extends ResourceLoader{

	public SimpleResourceLoader() {}
	@Override
	public final InputStream getResourceStream(URL url) throws IOException{
		return url.openConnection().getInputStream();
	}

}

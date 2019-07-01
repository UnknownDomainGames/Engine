package nullengine.util.security;

import org.objectweb.asm.commons.Method;

import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.jar.JarEntry;
import java.util.logging.Logger;

public class EngineSecurityManager extends SecurityManager {
	Logger logger=Logger.getGlobal();
	public void check(JarEntry jarEntry){
		Certificate[] certificates=jarEntry.getCertificates();
		for(Certificate certificate:certificates){
			PublicKey publicKey=certificate.getPublicKey();
			if(publicKey==null)
				logger.warning("WTF");
			else try{
				certificate.verify(publicKey);
			}
			 catch (CertificateException e){logger.warning("WTF");}
			 catch (NoSuchAlgorithmException e){logger.warning("WTF");}
			 catch (InvalidKeyException e){
				logger.exiting("EngineSecurityManager","check");
			 }
			 catch (NoSuchProviderException e){logger.warning("WTF");}
			 catch (SignatureException e){logger.warning("WTF");}
		}
	}
}

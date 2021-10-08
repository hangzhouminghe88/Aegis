package com.mingheinfo.common.utils;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERUTCTime;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.smime.SMIMECapabilitiesAttribute;
import org.bouncycastle.asn1.smime.SMIMECapability;
import org.bouncycastle.asn1.smime.SMIMECapabilityVector;
import org.bouncycastle.asn1.smime.SMIMEEncryptionKeyPreferenceAttribute;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.encodings.OAEPEncoding;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.mail.smime.SMIMEEnvelopedGenerator;
import org.bouncycastle.mail.smime.SMIMESigned;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;
import org.bouncycastle.mail.smime.SMIMEUtil;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;

import net.bytebuddy.asm.Advice.This;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

public class MimeUtils
{
    private static final String certAlias = "zs-ca-cert";
    private static final String E_MSG_NOT_SIGNED = "Message is not signed.";
    private static final String E_MSG_VERIFY = "Message cannot be verified.";
    
    public static MimeMessage createMimeMessage(Object content, String contentType){  
		try{  
	        Security.addProvider((Provider)new BouncyCastleProvider());
	        final Properties props = System.getProperties();
	        final Session session = Session.getDefaultInstance(props, (Authenticator)null);
	        
		    MimeMessage message = new MimeMessage(session);  
		    message.setContent(content, contentType);  
		    message.saveChanges();  
		    return message;  
		}catch(Exception e){  
		    e.printStackTrace(System.out);  
		    return null;  
		}  
    }
    
    public static MimeMultipart createMultipartWithSignature(PrivateKey key,   
            X509Certificate cert,   
            CertStore certsAndCRLs,  
            MimeBodyPart dataPart){  
		try{  
			ASN1EncodableVector         signedAttrs = new ASN1EncodableVector();  
			SMIMECapabilityVector       caps = new SMIMECapabilityVector();  
			caps.addCapability(SMIMECapability.aES256_CBC);  
			caps.addCapability(SMIMECapability.dES_EDE3_CBC);  
			caps.addCapability(SMIMECapability.rC2_CBC, 128);  
			signedAttrs.add(new SMIMECapabilitiesAttribute(caps));  
			signedAttrs.add(new SMIMEEncryptionKeyPreferenceAttribute(  
			SMIMEUtil.createIssuerAndSerialNumberFor(cert)));
						
			SMIMESignedGenerator gen = new SMIMESignedGenerator();  
			
			gen.addSigner(key, cert, SMIMESignedGenerator.DIGEST_SHA1,  
			new AttributeTable(signedAttrs), null);  

			gen.addCertificatesAndCRLs(certsAndCRLs);  
			
			MimeMultipart mulpart = gen.generate(dataPart, "BC");

			
			return mulpart;  
		}
		catch (Exception e){  
			e.printStackTrace(System.out);  
			return null;  
		}  
	}
    
    public static void doSignature(String data,HttpServletResponse response) throws Exception
    {        
    	Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    	
    	final KeyStore credentials = KeyStore.getInstance("JKS");
        credentials.load(loadPrivateCert(),"123456".toCharArray());
 
        PrivateKey      key = (PrivateKey)credentials.getKey("cloud","123456".toCharArray());  
        Certificate[]   chain = credentials.getCertificateChain("cloud");  
        CertStore       certsAndCRLs = CertStore.getInstance("Collection",  
                            new CollectionCertStoreParameters(Arrays.asList(chain)), "BC");  
  
        X509Certificate cert = (X509Certificate)chain[0];  

        MimeBodyPart   dataPart = new MimeBodyPart();  
        dataPart.setText(data);  
        dataPart.setHeader("Content-Type", "text/plain");
        dataPart.addHeader("Content-Transfer-Encoding", "base64");
        dataPart.addHeader("Content-Description","attachment");
        
        MimeMultipart multiPart = createMultipartWithSignature(key, cert, certsAndCRLs, dataPart);  

        String dd = multiPart.getContentType();
        
        MimeMessage mail = createMimeMessage(multiPart,multiPart.getContentType());
        mail.removeHeader("Message-ID");

        
        //File f= new File("d:\\1111.txt") ;
        response.setContentType("application/octet-stream; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=Minghe License.txt");
        OutputStream out = response.getOutputStream();
       	mail.writeTo(out);
       	out.flush();
        out.close();
    }
    
   
    public static String verifySignature(final InputStream signed, final X509Certificate cacert) throws Exception {
        Security.addProvider((Provider)new BouncyCastleProvider());
        final Properties props = System.getProperties();
        final Session session = Session.getDefaultInstance(props, (Authenticator)null);
        final KeyStore keystore = KeyStore.getInstance("JKS");
        keystore.load(null);
        keystore.setCertificateEntry("zs-ca-cert", cacert);
        final PKIXParameters param = new PKIXParameters(keystore);
        param.setRevocationEnabled(false);
        final MimeMessage msg = new MimeMessage(session, (InputStream)signed);
        if (!msg.isMimeType("multipart/signed")) {
            throw new Exception("Message is not signed.");
        }

        //String jsonstring = JSON.toJSONString(msg.getContent());
        //String jsonstring = msg.getContent().toString();
        MimeMultipart dd = (MimeMultipart)msg.getContent();

        final SMIMESigned smimesigned = new SMIMESigned((MimeMultipart)msg.getContent());
        final boolean ok = verifySignedMail(smimesigned, param);
        if (!ok) {
            throw new Exception("Message cannot be verified.");
        }
        return getContentFromSigned(smimesigned);
    }
    
    protected static boolean verifySignedMail(final SMIMESigned smimesigned, final PKIXParameters param) throws Exception {
        final Store certs = smimesigned.getCertificates();

        final SignerInformationStore signerinfo = smimesigned.getSignerInfos();
        final Collection<?> c = (Collection<?>)signerinfo.getSigners();
        final Iterator<?> it = c.iterator();
        final LinkedList<X509CertificateHolder> certHolders = new LinkedList<X509CertificateHolder>();
        while (it.hasNext()) {
            final SignerInformation signer = (SignerInformation)it.next();
            final Collection<?> certCollection = (Collection<?>)certs.getMatches((Selector)signer.getSID());
            for (final Object o : certCollection) {
                final X509CertificateHolder certHolder = (X509CertificateHolder)o;
                certHolders.add(certHolder);
                if (!signer.verify(new JcaSimpleSignerInfoVerifierBuilder().build(certHolder))) {
                    return false;
                }
            }
        }
        return certHolders.size() != 0;
    }
    
    protected static String getContentFromSigned(final SMIMESigned signed) throws IOException, MessagingException {
        final MimeBodyPart content = signed.getContent();
        final Object cont = content.getContent();
        return (cont instanceof String) ? ((String)cont) : null;
    }
    
    public static X509Certificate loadCert() throws CertificateException, NoSuchProviderException {
    	
    	InputStream caInput = MimeUtils.class.getClassLoader().getResourceAsStream("ca.pem");

        Security.addProvider((Provider)new BouncyCastleProvider());
        final CertificateFactory cf = CertificateFactory.getInstance("X.509", "BC");
        final X509Certificate cert = (X509Certificate)cf.generateCertificate(caInput);
        return cert;
    }
    
    public static InputStream loadPrivateCert() throws CertificateException, NoSuchProviderException {
    	
    	InputStream caInput = MimeUtils.class.getClassLoader().getResourceAsStream("private.jks");

        return caInput;
    }    
    
    public static final byte[] input2byte(InputStream inStream)		throws IOException {  
    	ByteArrayOutputStream swapStream = new ByteArrayOutputStream();  
    	byte[] buff = new byte[100];  
    	int rc = 0;  
    	while ((rc = inStream.read(buff, 0, 100)) > 0) {  
    		swapStream.write(buff, 0, rc);  
    	}  
    	byte[] in2b = swapStream.toByteArray();  
    	return in2b;  
	} 
   
}

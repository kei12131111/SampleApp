package sample.utils;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;



public class XmlUtils {

	
	public static Document readXml(String path) throws Exception{
		
		// 1. DocumentBuilderFactoryのインスタンスを取得する
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// 2. DocumentBuilderのインスタンスを取得する
		DocumentBuilder builder = factory.newDocumentBuilder();
		// 3. DocumentBuilderにXMLを読み込ませ、Documentを作る
		Document document = builder.parse(Paths.get(path).toFile());
		
		
		return document;		
	}

	
	
	public static void signature(Document xmlDom) {

	    try {
	        // 秘密鍵の取得
	        PrivateKey privateKey = getPrivateKeyFromDER();
	        // 証明書の取得
	        X509Certificate cert = getCertFromDER();

	        // 対象要素の親要素にID属性を付与する
	        Element targetNode = (Element)xmlDom.getElementsByTagName("saml:Assertion").item(0);
//	        Element parentNode = (Element)targetNode.getParentNode();
	        Attr idAttr = xmlDom.createAttribute("ID");
	        idAttr.setValue("_cb63bc36c2c03b4e1bcd5b1b0cc2e165d044546e88");
	        targetNode.setAttributeNode(idAttr);
	        targetNode.setIdAttribute("ID", true);

	        // 署名情報の設定
	        XMLSignatureFactory xmlSignFactory = XMLSignatureFactory.getInstance("DOM");

	        // 変換アルゴリズムの作成
	        ArrayList<Transform> refTransformList = new ArrayList<Transform>();
	        refTransformList.add(xmlSignFactory.newTransform(Transform.ENVELOPED, (TransformParameterSpec)null));
	        refTransformList.add(xmlSignFactory.newTransform(CanonicalizationMethod.EXCLUSIVE, (TransformParameterSpec)null));

	        // ダイジェスト計算アルゴリズムの生成
	        DigestMethod digestMethod = xmlSignFactory.newDigestMethod(DigestMethod.SHA256, null);

	        // 参照要素の設定
	        Reference ref = xmlSignFactory.newReference("#_cb63bc36c2c03b4e1bcd5b1b0cc2e165d044546e88", 
	                digestMethod, 
	                refTransformList, null, null);

	        // 正規化アルゴリズムの生成
	        CanonicalizationMethod cm = xmlSignFactory.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE, (C14NMethodParameterSpec)null);

	        // 署名アルゴリズムの生成
	        SignatureMethod sm = xmlSignFactory.newSignatureMethod("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", null);

	        // 署名情報の設定
	        SignedInfo signedInfo = xmlSignFactory.newSignedInfo(cm, sm, Collections.singletonList(ref));

	        KeyInfoFactory kif = xmlSignFactory.getKeyInfoFactory();
	        X509Data x509Data = kif.newX509Data(Collections.singletonList(cert));
	        KeyInfo keyInfo = kif.newKeyInfo(Collections.singletonList(x509Data));

	        // 署名対象と秘密鍵の設定
	        DOMSignContext dsc = new DOMSignContext(privateKey, targetNode);
	        dsc.setDefaultNamespacePrefix("ds");

	        // XML署名の実施
	        XMLSignature signature = xmlSignFactory.newXMLSignature(signedInfo, keyInfo);
	        signature.sign(dsc);

	        // コンソールに結果を表示する。
	        TransformerFactory tf = TransformerFactory.newInstance();
	        Transformer trans = tf.newTransformer();
	        
	        StringWriter sw = new StringWriter();
	        trans.transform(new DOMSource(xmlDom), new StreamResult(sw));
	        
	        String xmlStr = sw.toString();
	        System.out.println(xmlStr);
	        
	        try {
	            FileWriter fw = new FileWriter("src/sample/product2.xml",true);
	            PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
	            pw.println(xmlStr);
	            pw.close();
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        }
	        
	       
	        
	        

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	
	
	public static String signature2(Document xmlDom) {

	    try {
	        // 秘密鍵の取得
	        PrivateKey privateKey = getPrivateKeyFromDER();
	        // 証明書の取得
	        X509Certificate cert = getCertFromDER();

	        // 対象要素の親要素にID属性を付与する
	        Element targetNode = (Element)xmlDom.getElementsByTagName("samlp:Response").item(0);
//	        Element parentNode = (Element)targetNode.getParentNode();
	        Attr idAttr = xmlDom.createAttribute("ID");
	        idAttr.setValue("_8bcb622b78cd5395ffdac2b1bfee0595c3585f5e2d");
	        targetNode.setAttributeNode(idAttr);
	        targetNode.setIdAttribute("ID", true);

	        // 署名情報の設定
	        XMLSignatureFactory xmlSignFactory = XMLSignatureFactory.getInstance("DOM");

	        // 変換アルゴリズムの作成
	        ArrayList<Transform> refTransformList = new ArrayList<Transform>();
	        refTransformList.add(xmlSignFactory.newTransform(Transform.ENVELOPED, (TransformParameterSpec)null));
	        refTransformList.add(xmlSignFactory.newTransform(CanonicalizationMethod.EXCLUSIVE, (TransformParameterSpec)null));

	        // ダイジェスト計算アルゴリズムの生成
	        DigestMethod digestMethod = xmlSignFactory.newDigestMethod(DigestMethod.SHA256, null);

	        // 参照要素の設定
	        Reference ref = xmlSignFactory.newReference("#_8bcb622b78cd5395ffdac2b1bfee0595c3585f5e2d", 
	                digestMethod, 
	                refTransformList, null, null);

	        // 正規化アルゴリズムの生成
	        CanonicalizationMethod cm = xmlSignFactory.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE, (C14NMethodParameterSpec)null);

	        // 署名アルゴリズムの生成
	        SignatureMethod sm = xmlSignFactory.newSignatureMethod("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", null);

	        // 署名情報の設定
	        SignedInfo signedInfo = xmlSignFactory.newSignedInfo(cm, sm, Collections.singletonList(ref));

	        KeyInfoFactory kif = xmlSignFactory.getKeyInfoFactory();
	        X509Data x509Data = kif.newX509Data(Collections.singletonList(cert));
	        KeyInfo keyInfo = kif.newKeyInfo(Collections.singletonList(x509Data));

	        // 署名対象と秘密鍵の設定
	        DOMSignContext dsc = new DOMSignContext(privateKey, targetNode);
	        dsc.setDefaultNamespacePrefix("ds");

	        // XML署名の実施
	        XMLSignature signature = xmlSignFactory.newXMLSignature(signedInfo, keyInfo);
	        signature.sign(dsc);

	        // コンソールに結果を表示する。
	        TransformerFactory tf = TransformerFactory.newInstance();
	        Transformer trans = tf.newTransformer();
	        
	        StringWriter sw = new StringWriter();
	        trans.transform(new DOMSource(xmlDom), new StreamResult(sw));
	        
	        String xmlStr = sw.toString();
	        
	        System.out.println(xmlStr);
	        
	        xmlStr = xmlStr.replace("\r\n", "");
	        xmlStr = xmlStr.replace("    ", ""); 
	        
	        byte[] utf8 = xmlStr.getBytes("UTF-8");
	        
	        Charset charset = StandardCharsets.UTF_8;
//	        MessageDigest digester = MessageDigest.getInstance("SHA-256");
//	        digester.update(utf8);
//	        
//	        String xmlEncoded = Base64.getEncoder().encodeToString(digester.digest());
	        byte[]a = Base64.getEncoder()
	                .encode(xmlStr.getBytes(charset));
	        String xmlEncoded = new String(a, charset);
	        
	        return xmlEncoded;
	        
	        

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/**
	 * 秘密鍵の読込
	 * 
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws InvalidKeySpecException
	 */
	private static PrivateKey getPrivateKeyFromDER() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {

	    byte[] key = readkeyFile("src/sample/alice4.pk8");
	    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(key);

	    KeyFactory kf = KeyFactory.getInstance("RSA"); 
	    RSAPrivateKey privateKey = (RSAPrivateKey)kf.generatePrivate(keySpec);
	    return privateKey;
	}
	
	
	/**
	 * サーバー証明書の読込
	 * 
	 * @return
	 * @throws IOException
	 * @throws CertificateException
	 */
	private static X509Certificate getCertFromDER() throws IOException, CertificateException {

	    CertificateFactory cf = CertificateFactory.getInstance("X.509");
	    X509Certificate cert = (X509Certificate)cf.generateCertificate(new FileInputStream("src/sample/alice4.crt"));

	    return cert;
	}

	/**
	 * バイナリファイルの読込
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	private static byte[] readkeyFile(String fileName) throws IOException {
	    byte[] data = null;
	    FileInputStream input = new FileInputStream(fileName);
	    data = new byte[input.available()];
	    input.read(data);
	    input.close();

	    return data;
	}
	
}

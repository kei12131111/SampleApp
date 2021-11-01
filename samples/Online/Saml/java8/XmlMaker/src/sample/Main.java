package sample;

import org.w3c.dom.Document;

import sample.utils.SampleUtils;
import sample.utils.XmlUtils;

public class Main {

	public static void main(String[] args) throws Exception {
		
		
		String xmlPath = "src/sample/product.xml";
		
		Document document= XmlUtils.readXml(xmlPath);
		
		XmlUtils.signature(document);
		
		
//		String xmlPath2 = "src/sample/product2.xml";
//		
//		Document document2= XmlUtils.readXml(xmlPath2);
//		
//		String xmlEncoded = XmlUtils.signature2(document2);
//		
//		System.out.println(xmlEncoded);
		
		
//		SampleUtils.encode();

	}

}

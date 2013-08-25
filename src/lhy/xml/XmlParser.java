package lhy.xml;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class XmlParser {
	private static final String[] pos = 
		{ "n", "ns", "i", "j", "nh", "ni", "nl", "nt", "nz","m" };
	
	public static boolean isBelongTo(String s, String[] ss) {
		for (int i = 0; i < ss.length; i++) {
			if (s.equals(ss[i]))
				return true;
		}
		return false;
	}
	
	public static Vector<HashMap<String, Integer>> getKBEntities(String filename) throws DocumentException {
		
		Vector<HashMap<String, Integer>> KBEntities = new Vector<HashMap<String, Integer>>();
		
		File file = new File(filename);
		SAXReader reader = new SAXReader();
		Document doc = reader.read(file);
		Element root = doc.getRootElement();
		
		Element ioo, paraEle, sentEle, wordEle;
		Iterator<?> i = root.elementIterator(); 
		while (i.hasNext()) {
			ioo = (Element) i.next();
			Iterator<?> j = ioo.elementIterator(); 
			
			while (j.hasNext()) {
				HashMap<String, Integer> KBEntity = new HashMap<String, Integer>();
				
				paraEle = (Element) j.next();
				Iterator<?> k = paraEle.elementIterator(); 
				
				while (k.hasNext()) {
					sentEle = (Element) k.next();
					Iterator<?> l = sentEle.elementIterator(); 
					
					while (l.hasNext()) {
						wordEle = (Element) l.next();
						String word = wordEle.attribute("cont").getValue();
						
						if (isBelongTo(wordEle.attribute("pos").getValue(), pos)) {
							
							Integer count = KBEntity.get(word);
							if (count == null) {
								KBEntity.put(word, 1);
							} else {
								KBEntity.put(word, count + 1);
							}
						}
					}
				}
				KBEntities.add(KBEntity);
			}
		}
		
		return KBEntities;
	}

	public static Vector<HashMap<String, Integer>> getTrainEntities(String dirname) throws DocumentException, IOException {
		
		Vector<HashMap<String, Integer>> trainEntities = new Vector<HashMap<String, Integer>>();
		HashMap<String, Integer> trainEntity = new HashMap<String, Integer>();
		String word;
		File trainDir = new File(dirname);
        File directory = new File(trainDir.getAbsolutePath());
        File[] trainCataFiles = directory.listFiles();
		String filename;
		
		for (int num=0 ; num < trainCataFiles.length ; num++) {
			filename = dirname+"/"+trainCataFiles[num].getName();
			File f = new File(filename);
			
			SAXReader reader = new SAXReader();
			Document doc = reader.read(f);
			Element root = doc.getRootElement();
			Element ioo, paraEle, sentEle, wordEle;
			for (Iterator<?> i = root.elementIterator(); i.hasNext();)
			{
				ioo = (Element) i.next();
				for (Iterator<?> j = ioo.elementIterator(); j.hasNext();)
				{
					paraEle = (Element) j.next();
					for (Iterator<?> k = paraEle.elementIterator(); k.hasNext();)
					{
						sentEle = (Element) k.next();
						for (Iterator<?> l = sentEle.elementIterator(); l.hasNext();)
						{
							wordEle = (Element) l.next();
							word = wordEle.attribute("cont").getValue();
							if (XmlParser.isBelongTo(wordEle.attribute("pos").getValue(), pos))
							{
								if (trainEntity.get(word) == null) {
									trainEntity.put(word, 1);
								} 
								
								else {
									int count = trainEntity.get(word).intValue() + 1;
									trainEntity.put(word, count);
								}
							}
						}
					}
				}
			}
			trainEntities.add((HashMap<String, Integer>)trainEntity.clone());
			trainEntity.clear();
		}
		
		return trainEntities;
	}
}
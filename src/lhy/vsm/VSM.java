package lhy.vsm;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class VSM {
	
	public Vector<HashMap<String,Double>> calc_weights(Vector<HashMap<String,Integer>> docs) {
		int num_docs = docs.size();
		
		Vector<HashMap<String,Double>> weights_per_doc = new Vector<HashMap<String,Double>>();
		
		for(HashMap<String, Integer> doc : docs) {
			HashMap<String,Double> word_weights = new HashMap<String,Double>();
			Iterator<String> word_iter = doc.keySet().iterator();
			int doc_length = 0;
			
			while (word_iter.hasNext()) {
				String word = word_iter.next();
				doc_length += doc.get(word);
			}
			
			word_iter = doc.keySet().iterator();
			while (word_iter.hasNext()) {
				String word = word_iter.next();
				Integer word_weight = doc.get(word);
				int num_docs_contain = 0;
				
				for (int i = 0; i < num_docs; i++) {
					
					if (docs.get(i).get(word) != null) {
						num_docs_contain++;
					}
				}
				word_weights.put(word, word_weight.doubleValue() / (double) doc_length * Math.log((double) num_docs / (double) num_docs_contain));
			}
			weights_per_doc.add(word_weights);
		}
		return weights_per_doc;
	}
	
	public Double calc_sim(HashMap<String,Double> doc1, HashMap<String,Double> doc2) {
		Double len1 = 0d;
		Double len2 = 0d;
		Double value1;
		Double value2;
		Double valueDot = 0d;
		
		Iterator<Double> value_iter = doc1.values().iterator();
		while (value_iter.hasNext()) {
			double value = value_iter.next();
			len1 += value * value;
		}
		
	    value_iter = doc2.values().iterator();
		while (value_iter.hasNext()) {
			Double value = (Double) value_iter.next();
			len2 += value*value;
		}
		
		Iterator<String> word_iter = doc1.keySet().iterator();
		while (word_iter.hasNext()) {
			String key = word_iter.next();
			value1 = doc1.get(key);
			value2 = doc2.get(key);
			if(value2 == null)
				value2 = 0d;
			valueDot += value1 * value2;
		}
		
		return valueDot / ( Math.sqrt(len1) * Math.sqrt(len2));
	}
	
	public int find_most_similar(HashMap<String,Double> doc, Vector<HashMap<String,Double>> kb_weights, Double[] max_sim, int i)	{
		double sim; 
		max_sim[i] = 0d;
		int max = 0;
		
		for (int j = 0; j < kb_weights.size(); ++j) {
			sim = calc_sim(kb_weights.elementAt(j), doc);
			
			if(max_sim[i] < sim) {
				max_sim[i] = sim;
				max = j;
			}
		}
		
		return max + 1;
	}
	
	public double calc_pre(String filename, HashMap<Integer,Integer> resutls) throws IOException {
		int num_other_out = 0;
		int num_correct = 0;
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("main/ans/"+filename+".ans")));
		
		String line = in.readLine();
		while (line != null) {
			String doc_number_str;
			String entity_id_str;
			
			String[] parts = line.split("	");
			doc_number_str = parts[0];
			entity_id_str = parts[1];
			
			if (Character.isLetter(entity_id_str.charAt(0))) {
				++num_other_out;
			}
			
			else {
				int doc_number = Integer.parseInt(doc_number_str);
				int entity_id = Integer.parseInt(entity_id_str);
				
				int my_entity_id = resutls.get(doc_number);
				
				if (my_entity_id == entity_id) {
					num_correct++;
				}
			}
			
			line = in.readLine();
		}
		in.close();				
		
		return (double) num_correct / (double) (resutls.size() - num_other_out);
	}
}

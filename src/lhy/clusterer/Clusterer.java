package lhy.clusterer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import weka.clusterers.ClusterEvaluation;
import weka.clusterers.EM;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.core.converters.ConverterUtils.DataSink;

public class Clusterer {
	
	Instances m_instances;
	Vector<HashMap<String, Double>> m_docs;
	
	public Clusterer(Vector<HashMap<String, Double>> docs) {
		m_docs = docs;
		HashMap<String, Integer> word_index_map = new HashMap<String, Integer>();
		FastVector attributes = new FastVector();
		ArrayList<int[]> indexes_list = new ArrayList<int[]>();
		ArrayList<double[]> values_list = new ArrayList<double[]>();
		
		for (HashMap<String, Double> doc : docs) {
			// indexes need to be sorted
			TreeMap<Integer, Double> index_value_map = new TreeMap<Integer, Double>();
			
			Set<String> words = doc.keySet();
			for (String word : words) {
				if (!word_index_map.containsKey(word)) {
					attributes.addElement(new Attribute(word));
					word_index_map.put(word, word_index_map.size());
				}
				
				int index = word_index_map.get(word);
				double value = doc.get(word);
				index_value_map.put(index, value);
			}
			
			int[] indexes = new int[doc.size()];
			double[] values = new double[doc.size()];
			int i = 0;
			
			Set<Integer> index_set = index_value_map.keySet();
			for (int index : index_set) {
				double value = index_value_map.get(index);
				indexes[i] = index;
				values[i] = value;
				++i;
			}
			
			indexes_list.add(indexes);
			values_list.add(values);
		}
		
		m_instances = new Instances("dataset", attributes, 0);
		
		// add data
		for (int i = 0; i < indexes_list.size(); ++i) {
			SparseInstance sparseInstance = new SparseInstance(1.0d, values_list.get(i), indexes_list.get(i), m_instances.numAttributes());
			m_instances.add(sparseInstance);
		}
	}
	
	public void do_clustering() throws Exception {
		DataSink.write("dataset.arff", m_instances);
		String[] options = new String[4];
		options[0] = "-I"; // max. iterations
		options[1] = "100";
		options[2] = "-N";
		options[3] = "8";
		EM clusterer = new EM(); // new instance of clusterer
		clusterer.setOptions(options); // set the options
		clusterer.buildClusterer(m_instances); // build the clusterer
		
		HashMap<Integer, HashMap<String, Double>> clusters = new HashMap<Integer, HashMap<String, Double>>();
		for (int i = 0; i < m_instances.numInstances(); ++i) {
			int cluster = clusterer.clusterInstance(m_instances.instance(i));
			System.out.println(cluster);
		}
	}
	
}

package lhy.app;

import java.io.File;
import java.util.HashMap;
import java.util.Vector;

import lhy.clusterer.Clusterer;
import lhy.vsm.VSM;
import lhy.xml.XmlParser;

public class App {
	
	public static void main(String arge[]) throws Exception {
        String word = "白雪";

        HashMap<Integer, Integer> results = new HashMap<Integer, Integer>();

		VSM vsm = new VSM();

		Vector<HashMap<String, Integer>> trainEntities = XmlParser.getTrainEntities("main/train/" + word);
		Vector<HashMap<String,Double>> traintfidfs = vsm.calc_weights(trainEntities);

		Vector<HashMap<String, Integer>> KBEntities = XmlParser.getKBEntities( "main/kb/" + word);
		Vector<HashMap<String,Double>> KBtfidfs = vsm.calc_weights(KBEntities);

		double sum_max_sim = 0d;
		Double[] max_sim_array = new Double[traintfidfs.size()];
		for(int i = 0; i < traintfidfs.size(); ++i) {
			int result = vsm.find_most_similar(traintfidfs.elementAt(i), KBtfidfs, max_sim_array, i);
			results.put(i, result);
			sum_max_sim += max_sim_array[i];
		}
		double mean = sum_max_sim / traintfidfs.size();
		double std_dev = 0d;
		double sq_dev = 0d;
		for (int i = 0; i < max_sim_array.length; ++i) {
			sq_dev += Math.pow(max_sim_array[i] - mean, 2d);
		}
		std_dev = Math.sqrt(sq_dev / traintfidfs.size());

		System.out.println(mean + " " + std_dev);

		double precision = vsm.calc_pre(word, results);
		System.out.println(precision);

		Clusterer cl = new Clusterer(traintfidfs);
		cl.do_clustering();
	}
}

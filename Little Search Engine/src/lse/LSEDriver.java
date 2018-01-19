package lse;

import java.util.*;

public class LSEDriver {
	public static void main(String[] args) {
		LittleSearchEngine lse = new LittleSearchEngine();
		try {
			lse.makeIndex("docs.txt", "noisewords.txt");
		} catch(Exception e) {
			e.printStackTrace();
			return;
		}
		
		Set<String> results = lse.keywordsIndex.keySet();
		
		System.out.println("\n\n");
		for(String s : results) {
			System.out.println(s + " " + lse.keywordsIndex.get(s).toString());
		}
		
		ArrayList<String> top5 = lse.top5search("deep", "world");
		//ArrayList<String> top5 = lse.top5search("test", "test");
		if(top5 != null)
			System.out.println("\n\nResults: " + top5.toString());
		else
			System.out.println("\n\nResults: null");
	}
}

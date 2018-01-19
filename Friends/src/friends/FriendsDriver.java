package friends;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

public class FriendsDriver {
	
	public static void main(String[] args) throws FileNotFoundException{
		Scanner sc = new Scanner(new File("test3.txt"));
		Graph graph = new Graph(sc);		
		
		ArrayList<String> shortest = Friends.shortestChain(graph, "bmw", "audi");
		if(shortest == null) {
			System.out.println("Found no link");
		} else {
			for(String person : shortest) {
				System.out.println(person);
			}
		}
		System.out.println("\n\n");
		
		ArrayList<ArrayList<String>> cliques = Friends.cliques(graph, "foreign");
		if(cliques != null) {
			for(ArrayList<String> perClique : cliques) {
				System.out.println(perClique.toString());
			}
		}
		System.out.println("\n\n");
		
		ArrayList<String> connectors = Friends.connectors(graph);
		for(String connector : connectors) {
			System.out.println(connector);
		}
		
	}
	
}


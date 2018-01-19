package friends;

import structures.Queue;
import structures.Stack;

import java.util.*;

public class Friends {

	/**
	 * Finds the shortest chain of people from p1 to p2.
	 * Chain is returned as a sequence of names starting with p1,
	 * and ending with p2. Each pair (n1,n2) of consecutive names in
	 * the returned chain is an edge in the graph.
	 * 
	 * @param g Graph for which shortest chain is to be found.
	 * @param p1 Person with whom the chain originates
	 * @param p2 Person at whom the chain terminates
	 * @return The shortest chain from p1 to p2. Null if there is no
	 *         path from p1 to p2
	 */
	public static ArrayList<String> shortestChain(Graph g, String p1, String p2) {
		
		/** COMPLETE THIS METHOD **/
		boolean[] visited = new boolean[g.members.length];
		
		//Initialize queue to use for BFS
		Queue<Person> queue = new Queue<>();
		//Insert first element
		int index = g.map.get(p1);
		queue.enqueue(g.members[index]);
		
		//Create the corresponding queue of paths
		Queue<ArrayList<String>> paths = new Queue<>();
		//Initialize and enqueue first list
		ArrayList<String> firstList = new ArrayList<>();
		firstList.add(g.members[index].name);
		paths.enqueue(firstList);
		
		while(!queue.isEmpty()) {
			Person person = queue.dequeue();
			int personIndex = g.map.get(person.name);
			visited[personIndex] = true; //mark the person as visited
			
			//Get the path so far from queue
			ArrayList<String> list = paths.dequeue();

			Friend tmp = g.members[personIndex].first;
			while(tmp != null) {
				if(!visited[tmp.fnum]) {
					//Add the member to the path list to enqueue the new path later
					ArrayList<String> copy = new ArrayList<>(list);
					String name = g.members[tmp.fnum].name;
					copy.add(name);
					
					//Check if person 2 has been found, return list if so
					if(name.equals(p2)) {
						return copy;
					}
					
					//visited[tmp.data] = true; not necessary because we 				
					//mark nodes earlier in the outer loop
					queue.enqueue(g.members[tmp.fnum]);
					paths.enqueue(copy);
				}

				tmp = tmp.next;
			}
		}
		
		// FOLLOWING LINE IS A PLACEHOLDER TO MAKE COMPILER HAPPY
		// CHANGE AS REQUIRED FOR YOUR IMPLEMENTATION
		return null;
	}
	
	/**
	 * Finds all cliques of students in a given school.
	 * 
	 * Returns an array list of array lists - each constituent array list contains
	 * the names of all students in a clique.
	 * 
	 * @param g Graph for which cliques are to be found.
	 * @param school Name of school
	 * @return Array list of clique array lists. Null if there is no student in the
	 *         given school
	 */
	public static ArrayList<ArrayList<String>> cliques(Graph g, String school) {
		
		/** COMPLETE THIS METHOD **/
		boolean[] visited = new boolean[g.members.length];
		ArrayList<ArrayList<String>> allCliques = new ArrayList<>();
		
		for(int i = 0; i < g.members.length; i++) {
			Person p = g.members[i];
			if(visited[i] || !p.student) //Don't run from a position if it is not a student
				continue;
			
			ArrayList<String> newClique = new ArrayList<>();
			//System.out.println("Calling from main on " + g.members[i].name);
			cliqueDFS(g, visited, newClique, school, i);
			
			//Discard it if the clique is empty
			if(newClique != null && newClique.size() > 0)
				allCliques.add(newClique);
		}
		
		return allCliques;
	}
	
	private static void cliqueDFS(Graph graph, boolean[] visited, 
			ArrayList<String> cliqueMembers, String school, int index) {
		//Null base case taken care for by loop condition, won’t enter loop to begin with.
		//Method calls saved by checking inside the loop if a node has been visited
		Person person = graph.members[index];
		//System.out.println("Visiting " + person.name);
		
		//Add student to the list
		if(!visited[index] && person.student && person.school.equals(school))
			cliqueMembers.add(person.name);
		
		visited[graph.map.get(person.name)] = true;

		Friend curr = graph.members[index].first;
		while(curr != null) {
			int num = curr.fnum;
			Person friendPerson = graph.members[num];
			
			if(visited[num] == false && friendPerson.student
					&& friendPerson.school.equals(school)) {
				//System.out.println("Calling on " + friendPerson.name);
				cliqueDFS(graph, visited, cliqueMembers, school, num);
			}
			
			curr = curr.next;
		}
		
	}
	
	
	/**
	 * Finds and returns all connectors in the graph.
	 * 
	 * @param g Graph for which connectors needs to be found.
	 * @return Names of all connectors. Null if there are no connectors.
	 */
	public static ArrayList<String> connectors(Graph g) {
		
		/** COMPLETE THIS METHOD **/
		boolean[] visited = new boolean[g.members.length];
		ArrayList<String> allConnectors = new ArrayList<>();
		HashMap<String, Integer> dfsNums = new HashMap<>();
		HashMap<String, Integer> backNums = new HashMap<>();
		HashSet<String> backedUp = new HashSet<>();
		
		for(int i = 0; i < g.members.length; i++) {
			if(visited[i])
				continue;
			
			connectorDFS(g, visited, allConnectors, new int[] {0,0}, i,
					true, dfsNums, backNums, backedUp);
		}
		
		//return allCliques;
		// FOLLOWING LINE IS A PLACEHOLDER TO MAKE COMPILER HAPPY
		// CHANGE AS REQUIRED FOR YOUR IMPLEMENTATION
		return allConnectors;
		
	}
	
	private static void connectorDFS(Graph graph, boolean[] visited, 
			ArrayList<String> connectors, int[] nums, int index, boolean startingPoint,
			HashMap<String, Integer> dfsNums, HashMap<String, Integer> backNums,
			HashSet<String> backedUp) {
		//Null base case taken care for by loop condition, won’t enter loop to begin with.
		//Method calls saved by checking inside the loop if a node has been visited
		Person person = graph.members[index];		
		visited[graph.map.get(person.name)] = true;
		
		//Update the DFS and Back num
		dfsNums.put(person.name, nums[0]);
		backNums.put(person.name, nums[1]);

		Friend curr = graph.members[index].first;
		while(curr != null) {
			int personIndex = curr.fnum;
			Person friendPerson = graph.members[personIndex];
			
			if(!visited[personIndex]) {
				//increment dfs and backNum for next iteration
				nums[0]++;
				nums[1]++;
				
				connectorDFS(graph, visited, connectors, nums, personIndex,
						false, dfsNums, backNums, backedUp);
				
				if(dfsNums.get(person.name) > backNums.get(friendPerson.name)) {
					int minBack = Math.min(backNums.get(person.name), 
							backNums.get(friendPerson.name));
					
					backNums.put(person.name, minBack);
				}
				
				if(dfsNums.get(person.name) <= backNums.get(friendPerson.name)) {
					//if not null it's already been visited
					if(!startingPoint || backedUp.contains(person.name)) {
						if(!connectors.contains(person.name))
							connectors.add(person.name);
					}
				}
				
				backedUp.add(person.name);
				
			} else {
				//Neighbor has already been visited
				int minBack = Math.min(backNums.get(person.name), 
						dfsNums.get(friendPerson.name));
				
				backNums.put(person.name, minBack);
			}
			
			curr = curr.next;
		}
		
	}
}


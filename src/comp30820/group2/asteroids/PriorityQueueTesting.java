package comp30820.group2.asteroids;


import java.util.*; 

public class PriorityQueueTesting { 
	public static void main(String args[])  { 
		// Creating empty priority queue 
		PriorityQueue<String> strQueue = new PriorityQueue<String>(); 
		// add elements to numQueue using add() 
		strQueue.add("Five"); 
		strQueue.add("One"); 
		strQueue.add("Seven"); 
		strQueue.add("Three"); 
		strQueue.add("Eleven");
		strQueue.add("Nine"); 

		// Print the head element using Peek () method 
		System.out.println("Head element using peek method:"  + strQueue.peek()); 

		// Printing all elements 
		System.out.println("\n\nThe PriorityQueue elements:"); 
		Iterator iter1 = strQueue.iterator(); 
		while (iter1.hasNext()) 
			System.out.print(iter1.next() + " "); 

		// remove head with poll () 
		strQueue.poll(); 
		System.out.println("\n\nAfter removing an element" +  "with poll function:"); 
		Iterator<String> iter2 = strQueue.iterator(); 
		while (iter2.hasNext()) 
			System.out.print(iter2.next() + " "); 

		// Remove 'Three' using remove ()
		strQueue.remove("Three"); 
		System.out.println("\n\nElement 'Three' with"
				+ " remove function:"); 
		Iterator<String> iter3 = strQueue.iterator(); 

		while (iter3.hasNext()) 
			System.out.print(iter3.next() + " "); 

		// Check if an element is present in PriorityQueue using contains() 
		boolean ret_val = strQueue.contains("Five"); 
		System.out.println("\n\nPriority queue contains 'Five' "
				+ "or not?: " + ret_val); 

		// get array equivalent of PriorityQueue with toArray () 
		Object[] numArr = strQueue.toArray(); 
		System.out.println("\nArray Contents: "); 
		for (int i = 0; i < numArr.length; i++) 
			System.out.print(numArr[i].toString() + " "); 
	} 
}

import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class Worker implements Runnable {
	public static int nVertices;
	private static Deque<Integer> stack = new ConcurrentLinkedDeque<Integer>();
	private static Set<Integer> stack_contains = Collections.newSetFromMap(new ConcurrentHashMap<Integer, Boolean>(Worker.nVertices));
	private static Set<Integer> traversed = Collections.newSetFromMap(new ConcurrentHashMap<Integer, Boolean>(nVertices));
	private long started;
	private int id;
	private static AtomicInteger firstUntraversed;
	private static HashMap<Integer, Queue<Integer>> matrix;

	
	public static void setNVertices(int n) {
		nVertices = n;
	}
	
	public static void setFirstUntraversed(AtomicInteger fU) {
		firstUntraversed = fU;
	}
	
	public static void setMatrix(HashMap<Integer, Queue<Integer>> m) {
		matrix = m;
	}

    public Worker(int id) {
        this.started = System.currentTimeMillis();
    	this.id = id;
//    	this.firstUntraversed = firstUntraversed;
//        this.matrix = matrix;
    }
    
    @Override
    public void run() {
    	System.out.println("Thread-" + id + " started.");
    	
        while(Worker.traversed.size() < Worker.nVertices) {
        	Integer current = null;
        	Integer nextInList = Worker.firstUntraversed.getAndIncrement();
        	if(nextInList < Worker.nVertices) {
        		current = nextInList;
        	} else {
        		current = Worker.stack.pollLast();
        	}	
       		while(current != null) {
       			if(	Worker.traversed.contains(current) ){
	           		break;
	           	} else {
	           		Integer neighbour = Worker.matrix.get(current).poll();
	           		if( Worker.matrix.get(current).isEmpty() ) {
	           			Worker.traversed.add(current);
	    	           	System.out.println(current);
	           		}
	           		if( !stack_contains.contains(current) ){
	           			Worker.stack.add(current);
	           		}
	           		current = neighbour;
	            }
        	}

        	
        }
        System.out.println("Thread-" + id + " stopped.");
        System.out.println("Thread-" + id + " execution time was " + (System.currentTimeMillis() - started) + "ms");   
    }
}

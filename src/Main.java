import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {
	public static void main(String[] args) throws ParseException {
		Options options = new Options();
		options.addOption("q", "quiet", false, "Only execution time is shown.");
		options.addOption("t", "task", true, "Thread count.");
		options.addOption("i", true, "Input file.");
		options.addOption("o", true, "Output file.");
		options.addOption("n", true, "Matrix size.");
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(options, args);
		
		String inputFile = cmd.getOptionValue("i");
		String outputFile = cmd.getOptionValue("o");
		Integer threadCount = Integer.parseInt(cmd.getOptionValue("t"));
		boolean quietMode = cmd.hasOption("q");
		String matrixSize = cmd.getOptionValue("n");
		
		long start = System.currentTimeMillis();
//		Scanner s = new Scanner(System.in);
//		String input = s.nextLine();

		if(inputFile != null){
			String filename = "/home/emo/Desktop/Work/try.in";
			BufferedReader br = null;
			FileReader fr = null;
			try {
				String sCurrentLine;

				br = new BufferedReader(new FileReader(filename));
				int n = Integer.parseInt(br.readLine());
				HashMap<Integer, Queue<Integer>> matrix = new HashMap<Integer, Queue<Integer>>(n);
				
				int row = 0;
				while ((sCurrentLine = br.readLine()) != null) {
					
					String[] connections = sCurrentLine.split("\t"); 							//fix
					for (int i = 0; i < connections.length; i++) {
						if(connections[i].equals("1")) {
							if(!matrix.containsKey(row)) {
								matrix.put(row, new ConcurrentLinkedQueue<Integer>());
							}
							if(row < i) {
								matrix.get(row).add(i);
							}
						}
					}
					row++;
				}
				
				long preProccesing = System.currentTimeMillis() - start;
				System.out.println("Build up time -> " + preProccesing +"ms");

				
				AtomicInteger firstUntraversed = new  AtomicInteger(0);
				
				Worker.setNVertices(n);
				Worker.setFirstUntraversed(firstUntraversed);
				Worker.setMatrix(matrix);

				ExecutorService service = Executors.newFixedThreadPool(threadCount);
				
				for (int i = 0; i < threadCount; i++) {
		            Worker w = new Worker(i);
		            service.execute(w);
		        }

				try {
					service.awaitTermination( 2000, TimeUnit.NANOSECONDS);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IOException e) { 
				e.printStackTrace();
			} finally {
				try {
					if (br != null)
						br.close();
					if (fr != null)
						fr.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}

			}
		} else {
			//Generate file
		}
	}
}

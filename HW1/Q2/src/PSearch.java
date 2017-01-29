//UT-EID=tam2983
// amt3639


import java.util.*;
import java.util.concurrent.*;


public class PSearch implements Callable<Integer>{
	 public static ExecutorService threadPool;
	  private int start;
	  private int end;
	  private int k;
	  private int[] A;
	  
	
	public static int parallelSearch(int k, int[] A, int numThreads){
	  
	 
    
	  try {
		  int j=-1;
          ExecutorService es = Executors.newSingleThreadExecutor();
          threadPool = Executors.newCachedThreadPool();
          int numElements=A.length / numThreads;
          int extras = A.length%numThreads;
          int i=0;
          HashSet<Future<Integer>> futureHash = new HashSet();
          while(i<A.length){
        	  if(extras > 0){
        		  futureHash.add(threadPool.submit(new PSearch(k,i,i+numElements+1,A)));  
        		  extras--;
        		  i += numElements+1;
        	  } else {
        		  futureHash.add(threadPool.submit(new PSearch(k,i,i+numElements,A)));
        		  i+= numElements;
        	  }
          }
          for(Future<Integer> f: futureHash) {
        	  j = f.get();
        	  if(j != -1)
        		  break;
        
          }
          es.shutdown();
          PSearch.threadPool.shutdown();
          return j;
      } catch (Exception e) {
          e.printStackTrace();
      }
	  
    return -1; // if not found
  }
  
  public PSearch(int k, int start, int end, int[] A){
	  this.k=k;
	  this.start=start;
	  this.end=end;
	  this.A=A;
  }
  
  @Override
  public Integer call(){
	  
	  for(int counter=start; counter<end;counter++){
		  if(A[counter]==k){//found the number
			  return counter;
		  }
	  }
	  return -1;
  }
}

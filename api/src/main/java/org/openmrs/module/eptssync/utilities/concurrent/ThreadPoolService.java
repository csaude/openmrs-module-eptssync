package org.openmrs.module.eptssync.utilities.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.logging.Log;
import org.openmrs.module.eptssync.exceptions.ForbiddenOperationException;
import org.openmrs.module.eptssync.utilities.CommonUtilities;

/**
 * @author jpboane
 *
 */
public class ThreadPoolService {
	private static CommonUtilities utilities = CommonUtilities.getInstance();

	private static ThreadPoolService service;
	
	private List<ExecutorServiceManager> createdThreadPools;
	
	private ThreadPoolService(){
		super();
		
		
		this.createdThreadPools = new ArrayList<ExecutorServiceManager>();
	}
	
    public static synchronized ThreadPoolService getInstance() {
        if (service == null) {
            service = new ThreadPoolService();
        }
        
        return service;
    }
    
    private ExecutorServiceManager retrieveExistingExecutor(String threadId) {
    	return ExecutorServiceManager.find(this.createdThreadPools, threadId);
    }
    
    public void terminateTread(Log logger, Level logLevel, String threadId, Runnable runnable) {
    	utilities.logDebug("TRYING TO TERMINATE THREAD [" + threadId + "]", logger, logLevel);
    	
    	ExecutorServiceManager manager = retrieveExistingExecutor(threadId);
    	
    	if (manager != null) {
    		ThreadPoolExecutor service = (ThreadPoolExecutor) manager.getExecutorService();
    	    
    		service.remove(runnable);
    		
    		List<Runnable> a = service.shutdownNow();
    		
    		if (a != null && a.size() > 1) throw new ForbiddenOperationException("There were thread awating... " + a);
    	
    		utilities.logDebug("THREAD [" + threadId + "] WAS TERMINATED SUCCESSIFULY!", logger, logLevel);
    		
    		this.createdThreadPools.remove(manager);
       }
    	else {
    		utilities.logWarn("THREAD [" + threadId + "] WAS NOT FOUND  IN THREAD POOL!!!!", logger, logLevel);
     	}
    }
	
   /* public void removeRoutine(Log logger, Level logLevel, String threadId, Runnable runnable) {
    	utilities.logDebug("TRYING TO REMOVE ROUTINE FROM THREAD POOL[" + threadId + "]", logger, logLevel);
    	
    	ExecutorServiceManager manager = retrieveExistingExecutor(threadId);
    	
    	if (manager != null) {
    		
        	ThreadPoolExecutor service = (ThreadPoolExecutor) manager.getExecutorService();
        
    		boolean removed = service.remove(runnable);
    	
    		if (removed) utilities.logDebug("ROUTINE SUCCESSIFULY REMOVED FROM THREAD [" + threadId + "]!", logger, logLevel);
    		else utilities.logWarn("THE ROUTINE WAS NOT FOUND ON THREAD [" + threadId + "]!", logger, logLevel);
    		
    		
    		this.createdThreadPools.remove(null);
       }
    	else {
    		utilities.logErr("THREAD [" + threadId + "] WAS NOT FOUND  ON REGISTRED THREAD POOLS!!!!", logger, logLevel);
     	}
    }*/
	
	public synchronized ExecutorService createNewThreadPoolExecutor(String namingPattern){
		ExecutorServiceManager existingManager = ExecutorServiceManager.find(this.createdThreadPools, namingPattern);
		
		if (existingManager != null) return existingManager.getExecutorService();
		
		BasicThreadFactory threadFactory = new BasicThreadFactory.Builder()
				.namingPattern(namingPattern)
				.daemon(true)
				.priority(Thread.MAX_PRIORITY)
				.build();
		
		
		this.createdThreadPools.add(new ExecutorServiceManager(Executors.newCachedThreadPool(threadFactory), namingPattern));
		
		ThreadPoolExecutor eService = (ThreadPoolExecutor) this.createdThreadPools.get(this.createdThreadPools.size()-1).getExecutorService();
		
		return eService;
	}
}

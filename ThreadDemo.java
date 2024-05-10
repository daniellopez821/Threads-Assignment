package advanced_week_13;

import java.util.concurrent.*;
import java.util.concurrent.locks.*;


public class ThreadDemo {
	
	public static class Account{
		private static Lock lock = new ReentrantLock();
		
		private Condition newDeposit = lock.newCondition();
		
		private Condition newWithdraw = lock.newCondition();
		
		private int balance = 0;
		
		public void deposit(int num) {
			lock.lock();
			
			balance += num;
			System.out.print("Deposit " + num);
			System.out.println("\t\t\t" + balance);
			
			
			newDeposit.signalAll();
			
			lock.unlock();
		}
		
		public void withdraw(int num) {
			lock.lock();
			
			while(balance < num) {
				try {
					System.out.print("\t\tWaiting\n");
					newWithdraw.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			balance -= num;
			System.out.print("\t\tWithdraw " + num);
			System.out.println("\t" + balance);
			
			lock.unlock();
		}
	}
	
	private static Account account = new Account();
	
	public static class deposits implements Runnable{
		int min = 1;
		int max = 10;
		
		int num = Math.round((int) (Math.random()* ((max-min) + 1)) + min);
		
		@Override
		public void run() {
			account.deposit(num);
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public static class withdrawals implements Runnable{
		int min = 1;
		int max = 10;
		
		int num = Math.round((int) (Math.random()* ((max-min) + 1)) + min);
		
		@Override
		public void run() {
			account.withdraw(num);
			
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	
	public static void main(String[] args) {
		ExecutorService executor = Executors.newCachedThreadPool();
		
		System.out.println("Thread1\t\tThread2\t\tBalance");
		
		for(int i = 0; i < 5; i++) {
			executor.execute(new deposits());
			executor.execute(new withdrawals());
		}
		
	}
}

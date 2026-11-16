public class ClinicDoctors {
	static MySemaphore pool = new MySemaphore(2);
	static MySemaphore mutex = new MySemaphore(1);
	static int busy = 0;
	static int maxBusy = 0;
	static int total = 0;
	static int[] uses = new int[6];
	static class MySemaphore {
		private int permits;
		public MySemaphore (int permits) {
			this.permits =  permits;
		}
		public synchronized void acquire() throws InterruptedException {
			while (permits == 0) {
				wait();
			}
			permits--;
		}
		public synchronized void release() {
			permits++;
			notifyAll();
		}
	}
	static class Patient extends Thread {
		int id;
		int visits;

		public Patient(int id, int visits) {
			this.id = id;
			this.visits = visits;
		}

		public void run() {
			for (int i = 0 ; i < visits ; i++) {
				try {
					pool.acquire();
					mutex.acquire();
					busy++;
					if (busy > maxBusy) {
						maxBusy = busy;
					}
					uses[id-1]++;
					total++;
					mutex.release();
					Thread.sleep(20);

					mutex.acquire();
					busy--;
					mutex.release();
					pool.release();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public static void main(String[] args) {
		int[] visits = {2,3,2,4,3,2};
		Patient[] patients = new Patient[6];
		for (int i = 0 ; i < 6 ; i++) {
			patients[i] = new Patient(i + 1, visits[i]);
			patients[i].start();
		}
		for (int i = 0; i < 6; i++) {
			try {
				patients[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("total " + total);
		for (int i = 0; i < 6; i++) {
			System.out.println("Patient " + (i + 1) + ": " + uses[i]);
		}
		System.out.println("Maximum busy doctors: " + maxBusy);
	}
}
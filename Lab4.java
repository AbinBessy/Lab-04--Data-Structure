package com.example;

@SuppressWarnings("CallToPrintStackTrace")
public class Lab4 {

    public static void main(String[] args) {
        BankAccount bankAccount = new BankAccount(0);
        DepositRunnable depositRunnable = new DepositRunnable(bankAccount);
        WithdrawRunnable withdrawRunnable = new WithdrawRunnable(bankAccount);

        try {
            Thread depositThread = new Thread(depositRunnable);
            Thread withdrawThread = new Thread(withdrawRunnable);

            depositThread.start();
            withdrawThread.start();

            depositThread.join();
            withdrawThread.join();

            System.out.println("\n\nFinal balance: " + bankAccount.getBalance());
            bankAccount.getStats();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class BankAccount {
        private int balance;
        private int totalDepositsMade;
        private int totalWithdrawalsMade;

        public BankAccount(int balance) {
            this.balance = balance;
            this.totalDepositsMade = 0;
            this.totalWithdrawalsMade = 0;
        }

        public synchronized void deposit(int amount) {
            balance += amount;
            totalDepositsMade++;
        }

        public synchronized void withdraw(int amount) {
            if (balance >= amount) {
                balance -= amount;
                totalWithdrawalsMade++;
            } else {
                System.out.println("Insufficient balance for withdrawal.");
            }
        }

        public synchronized int getBalance() {
            return balance;
        }

        public synchronized void getStats() {
            System.out.println("Total deposits made: " + totalDepositsMade);
            System.out.println("Total withdrawals made: " + totalWithdrawalsMade);
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public static class DepositRunnable implements Runnable {
        private final BankAccount bankAccount;

        public DepositRunnable(BankAccount bankAccount) {

            this.bankAccount = bankAccount;

        }

        @Override
        public void run() {
            System.out.println("Deposit starts here...");

            for (int i = 0; i < 10; i++) {
                synchronized (bankAccount) {
                    bankAccount.deposit(1);
                    System.out.println("$1 : New balance: " + bankAccount.getBalance());
                    bankAccount.notify();
                }
                try {
                    Thread.sleep(1000); // Sleep for 1 second
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("Deposit process completed.");
        }
    }

    public static class WithdrawRunnable implements Runnable {
        private final BankAccount bankAccount;

        public WithdrawRunnable(BankAccount bankAccount) {
            this.bankAccount = bankAccount;
        }

        @Override
        public void run() {
            System.out.println("Withdrawal starts here...");

            for (int i = 0; i < 5; i++) {
                synchronized (bankAccount) {
                    while (bankAccount.getBalance() < 2) {
                        try {
                            bankAccount.wait(); // Wait for sufficient balance
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    bankAccount.withdraw(2);
                    System.out.println("$2: New balance: " + bankAccount.getBalance());
                }
            }

            System.out.println("Withdrawal Process Completed.");
        }
    }
}

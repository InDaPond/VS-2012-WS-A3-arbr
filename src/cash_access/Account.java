package cash_access;

public abstract class Account
{
    /**
     * Withdraws a specific amount of Money from the Account
     * @param amount Money that shall be withdrawn from the Account
     * @throws OverdraftException Throws an Exception if the Account is overdrawn
     * @throws InvalidAmountException 
     */
    public abstract void withdraw(double amount) throws OverdraftException;
    /**
     * Deposits a specific amount of Money on the Account
     * @param amount Money that shall be deposited on the Account
     * @throws InvalidAmountException 
     */
    public abstract void deposit(double amount);
    /**
     * Get's the current balance of the Account back
     * @return Balance of the Account
     */
    public abstract double getBalance();
}

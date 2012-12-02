package branch_access;

public abstract class Manager
{
    /**
     * Creates a new Account for a specific owner
     * @param owner Name of the Account-Owner
     * @return String 
     */
    public abstract String createAccount(String owner);
    /**
     * Get's the Balance of an Account back
     * @param accountID Account ID, needed to get the Balance back
     * @return Balance of the Account as double
     */
    public abstract double getBalance(String accountID);
}

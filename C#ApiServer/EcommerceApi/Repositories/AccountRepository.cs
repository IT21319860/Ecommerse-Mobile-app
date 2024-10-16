using EcommerceApi.Data;
using EcommerceApi.Models;
using MongoDB.Driver;

namespace EcommerceApi.Repositories
{
    public class AccountRepository
    {
        private readonly IMongoCollection<Account> Aaccounts;

        public AccountRepository(MongoDbContext context)
        {
            Aaccounts = context.Accounts;
        }

        public async Task<List<Account>> GetAllAccountsAsync()
        {
            return await Aaccounts.Find(account => true).ToListAsync();
        }

        public async Task<Account> GetAccountByEmailAsync(string email)
        {
            return await Aaccounts.Find(account => account.Email == email).FirstOrDefaultAsync();
        }
        public async Task<Account> GetAccountByIdAsync(string id)
        {
            return await Aaccounts.Find(a => a.Id == id).FirstOrDefaultAsync();
        }

        public async Task CreateAccountAsync(Account account)
        {
            await Aaccounts.InsertOneAsync(account);
        }

        public async Task UpdateAccountAsync(string id, Account account)
        {
            await Aaccounts.ReplaceOneAsync(acc => acc.Id == id, account);
        }

        public async Task DeleteAccountAsync(string id)
        {
            await Aaccounts.DeleteOneAsync(acc => acc.Id == id);
        }
    }
}


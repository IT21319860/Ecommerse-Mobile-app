using MongoDB.Driver;
using Microsoft.Extensions.Options;
using EcommerceApi.Models;

namespace EcommerceApi.Data
{
    // DatabaseSettings class to hold MongoDB connection information
    public class DatabaseSettings
    {
        public string MongoDb { get; set; }
        public string DatabaseName { get; set; }
    }

    public class MongoDbContext
    {
        private readonly IMongoDatabase Ddatabase;

        public MongoDbContext(IOptions<DatabaseSettings> settings)
        {
            // Initialize MongoClient and connect to the database
            var client = new MongoClient(settings.Value.MongoDb);  // Use MongoDb connection string from settings
            Ddatabase = client.GetDatabase(settings.Value.DatabaseName);  // Select the database name
        }

        // Define the collection for Accounts
        public IMongoCollection<Account> Accounts => Ddatabase.GetCollection<Account>("Accounts");

        // Define the collection for feedback
        public IMongoCollection<Feedback> Feedbacks => Ddatabase.GetCollection<Feedback>("Feedbacks");
    }
}


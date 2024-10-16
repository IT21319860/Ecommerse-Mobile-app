using EcommerceApi.Data;
using EcommerceApi.Models;
using MongoDB.Driver;

namespace EcommerceApi.Repositories
{
    public class FeedbackRepository
    {
        private readonly IMongoCollection<Feedback> FfeedbackCollection;

        public FeedbackRepository(MongoDbContext context)
        {
            // Get the feedback collection from the MongoDbContext
            FfeedbackCollection = context.Feedbacks;
        }

        // Method to add new feedback
        public async Task AddFeedbackAsync(Feedback feedback)
        {
            await FfeedbackCollection.InsertOneAsync(feedback);
        }
    }
}
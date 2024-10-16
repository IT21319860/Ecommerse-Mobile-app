using EcommerceApi.Models;
using EcommerceApi.Repositories;
using Microsoft.AspNetCore.Mvc;

namespace EcommerceApi.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class FeedbackController : ControllerBase
    {
        private readonly FeedbackRepository FfeedbackRepository;

        public FeedbackController(FeedbackRepository feedbackRepository)
        {
            FfeedbackRepository = feedbackRepository;
        }

        // POST: api/feedback - To add new feedback
        [HttpPost]
        public async Task<IActionResult> AddFeedback([FromBody] Feedback feedback)
        {
            if (feedback == null)
            {
                return BadRequest("Feedback cannot be null.");
            }

            await FfeedbackRepository.AddFeedbackAsync(feedback);
            return Ok("Feedback submitted successfully.");
        }
    }
}
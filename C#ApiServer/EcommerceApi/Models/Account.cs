namespace EcommerceApi.Models
{
    public class Account
    {
        public string Id { get; set; }  // MongoDB will use this as the unique identifier
        public string Email { get; set; }
        public string Password { get; set; }
        public bool IsApproved { get; set; } = false;  // Default to not approved
        
    }
}

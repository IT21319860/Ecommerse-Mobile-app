using EcommerceApi.Models;
using EcommerceApi.Repositories;
using Microsoft.AspNetCore.Mvc;
using System.Threading.Tasks;

namespace EcommerceApi.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class AccountsController : ControllerBase
    {
        private readonly AccountRepository _accountRepository;

        public AccountsController(AccountRepository accountRepository)
        {
            _accountRepository = accountRepository;
        }
        // Endpoint to create an account
        [HttpPost]
        public async Task<IActionResult> CreateAccount([FromBody] Account account) //Creating Account or Registering a user
        {
            var existingAccount = await _accountRepository.GetAccountByEmailAsync(account.Email);
            if (existingAccount != null)
            {
                return BadRequest("Account already exists");
            }

            await _accountRepository.CreateAccountAsync(account);
            return Ok();
        }
        // Endpoint to login an account
        [HttpPost("login")]
        public async Task<IActionResult> Login([FromBody] Account account)  //login in a user account
        {
            var existingAccount = await _accountRepository.GetAccountByEmailAsync(account.Email);
            if (existingAccount == null || existingAccount.Password != account.Password)
            {
                return Unauthorized("Invalid credentials");
            }

            if (!existingAccount.IsApproved)
            {
                return Unauthorized("Account not approved");
            }

            return Ok();
        }
        // Endpoint to Approve an account
        [HttpPut("{id}/approve")]  
        public async Task<IActionResult> ApproveAccount(string id)  //approving a user account
        {
            var account = await _accountRepository.GetAccountByIdAsync(id);
            if (account == null)
            {
                return NotFound();
            }

            account.IsApproved = true;
            await _accountRepository.UpdateAccountAsync(id, account);
            return Ok();
        }
        // Endpoint to Deactivate an account
        [HttpPut("{id}/deactivate")]  
        public async Task<IActionResult> DeactivateAccount(string id)  // deactivating a user account
        {
            var account = await _accountRepository.GetAccountByIdAsync(id);
            if (account == null)
            {
                return NotFound();
            }

            account.IsApproved = false;
            await _accountRepository.UpdateAccountAsync(id, account);
            return Ok();
        }

        // Endpoint to Delete an account
        [HttpDelete("{id}/reject")]
        public async Task<IActionResult> RejectAccount(string id)  //admin rejects a user account
        {
            var account = await _accountRepository.GetAccountByEmailAsync(id);
            if (account == null)
            {
                return NotFound();
            }

            await _accountRepository.DeleteAccountAsync(id);
            return Ok("Account rejected and deleted");
        }

        // Endpoint to reactivate an account
        [HttpPut("{id}/reactivate")]
        public async Task<IActionResult> ReactivateAccount(string id)  //CSR or Admin Reactivate account
        {
            var account = await _accountRepository.GetAccountByIdAsync(id);
            if (account == null)
            {
                return NotFound();
            }

            account.IsApproved = true;  // Reactivate by approving the account again
            await _accountRepository.UpdateAccountAsync(id, account);
            return Ok("Account reactivated");
        }

    }
}

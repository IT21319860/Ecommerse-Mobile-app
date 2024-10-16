var builder = WebApplication.CreateBuilder(args);

// Add services to the container.

// Register MongoDB connection settings from appsettings.json
builder.Services.Configure<EcommerceApi.Data.DatabaseSettings>(
    builder.Configuration.GetSection("ConnectionStrings"));

// Add the MongoDB context as a singleton service
builder.Services.AddSingleton<EcommerceApi.Data.MongoDbContext>();

// Register the AccountRepository as a scoped service
builder.Services.AddScoped<EcommerceApi.Repositories.AccountRepository>();
builder.Services.AddScoped<EcommerceApi.Repositories.FeedbackRepository>();

// Add controllers
builder.Services.AddControllers();

// Enable API documentation with Swagger
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI(c => c.SwaggerEndpoint("/swagger/v1/swagger.json", "EcommerceApi v1"));
}

app.UseHttpsRedirection();

app.UseAuthorization();

app.MapControllers();

app.Run();

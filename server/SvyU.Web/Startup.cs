using System;
using Microsoft.AspNetCore.Components.Builder;
using Microsoft.Extensions.DependencyInjection;
using SvyU.Web.Services;
using ZXing;

namespace SvyU.Web
{
    public class Startup
    {
        public void ConfigureServices(IServiceCollection services)
        {
            services.AddSingleton(new Random((int)DateTimeOffset.UtcNow.Ticks));
            services.AddTransient<SurveyService>();
            services.AddTransient(_ => new BarcodeWriterSvg()
            {
                Format = BarcodeFormat.QR_CODE,
                Options = new ZXing.Common.EncodingOptions()
                {
                    Margin = 1
                }
            });
        }

        public void Configure(IComponentsApplicationBuilder app)
        {
            app.AddComponent<App>("app");
        }
    }
}

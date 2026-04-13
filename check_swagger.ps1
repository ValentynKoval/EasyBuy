$ErrorActionPreference = 'Stop'

$r1 = Invoke-WebRequest -Uri 'http://localhost:8081/swagger-ui.html' -UseBasicParsing -TimeoutSec 15
$r2 = Invoke-WebRequest -Uri 'http://localhost:8081/v3/api-docs' -UseBasicParsing -TimeoutSec 15

@(
  'swagger-ui status=' + $r1.StatusCode
  'swagger-ui uri=' + $r1.BaseResponse.ResponseUri.AbsoluteUri
  'api-docs status=' + $r2.StatusCode
  $r2.Content.Substring(0, [Math]::Min(200, $r2.Content.Length))
) | Set-Content -Path 'C:\Java\ProgectCV\Progect\EasyBuy\swagger_check_current.log' -Encoding utf8


$p='src/main/resources/templates/auth/student-register.html'
$h=Get-Content 'src/main/resources/templates/auth/employer-register.html' -Raw
$h=$h -replace 'Employer Registration','Student Registration'
$h=$h -replace '/employer/register','/student/register'
$h=$h -replace 'ROLE_EMPLOYER','ROLE_JOBSEEKER'
$h=$h -replace 'Create Employer Account','Create Student Account'
$h=$h -replace 'top talent for your organization','the perfect career'
$h=$h -replace 'Start hiring','Start your journey to finding'
$h=$h -replace 'Work Email','Email Address'
$h=$h -replace 'you@company.com','you@example.com'
$h=$h -replace 'fa-building','fa-user-graduate'
Set-Content $p $h -Encoding UTF8
Write-Host 'OK'

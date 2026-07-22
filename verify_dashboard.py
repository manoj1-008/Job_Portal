import re

with open('src/main/resources/templates/employer/dashboard.html', encoding='utf-8') as f:
    c = f.read()

print(f'File size: {len(c)} bytes')

opens = len(re.findall(r'<div(?:\s|>)', c))
closes = c.count('</div>')
status = "OK" if opens == closes else "MISMATCH"
print(f'divs: {opens} open, {closes} close - {status}')

span_o = len(re.findall(r'<span(?:\s|>)', c))
span_c = c.count('</span>')
status = "OK" if span_o == span_c else "MISMATCH"
print(f'spans: {span_o} open, {span_c} close - {status}')

tags = re.findall(r'<(/?)(\w+)', c)
void = {'br','hr','img','input','meta','link','DOCTYPE','canvas','!DOCTYPE'}
stack = []
for is_close, tag in tags:
    if tag in void:
        continue
    if not is_close:
        stack.append(tag)
    elif stack and stack[-1] == tag:
        stack.pop()
    else:
        stack.append(f'UNMATCHED_CLOSE_{tag}')

status = "PASS" if len(stack) == 0 else "FAIL"
print(f'Tag stack remaining: {len(stack)} - {status}')
if stack:
    print(f'  Issues: {stack[:10]}')

# Check model attributes
for attr in ['stats', 'recentJobs', 'currentUser', 'totalJobs']:
    found = attr in c
    print(f'  Model attribute [{attr}] found: {"YES" if found else "NO"}')

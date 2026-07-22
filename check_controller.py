with open('src/main/java/com/onlinejobportal/controller/StudentController.java') as f:
    content = f.read()

idx = content.find('@GetMapping("/resume")')
print(f'Before @GetMapping("/resume"): repr={repr(content[idx-12:idx])}')

idx2 = content.find('return "student/settings"')
print(f'Settings return context: repr={repr(content[idx2-6:idx2+30])}')
print()
print('Lines 310-320:')
lines = content.split('\n')
for i, line in enumerate(lines[309:320], start=310):
    print(f'{i}: {repr(line)}')

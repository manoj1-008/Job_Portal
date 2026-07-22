import os

path = os.path.join('src', 'main', 'resources', 'templates', 'jobs', 'detail.html')

html = '<!DOCTYPE html>\n'
html += '<html xmlns:th="http://www.thymeleaf.org" lang="en">\n'
html += '<head th:replace="~{fragments/header::head(\'\' + ${job.title()} + \' at \' + ${job.company()} + \' - JobPortal\', \'View job details\')}"></head>\n'
html += '<body>\n'
html += '<div class="aurora-bg"></div>\n'
html += '<div class="aurora-blob aurora-blob-1"></div>\n'
html += '<div class="aurora-blob aurora-blob-2"></div>\n'
html += '<div class="mouse-glow"></div>\n'
html += '<nav th:replace="~{fragments/header::navbar}"></nav>\n'
html += '\n'

with open(path, 'w', encoding='utf-8') as f:
    f.write(html)

print('Base file created')

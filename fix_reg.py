import sys

if len(sys.argv) < 2:
    print('Usage: python fix_reg.py <filename> <content>.base64>')
    sys.exit(1)

fname = sys.argv[1]
content = base64.b64decode(sys.argv[2]).decode('utf-8')
open(fname, 'w', encoding='utf-8').write(content)
print(f'Wrotten {len(content)} bytes to {fname}')
import os
import re

def remove_comments(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    original_content = content
    ext = os.path.splitext(file_path)[1].lower()

    if ext in ['.java', '.kt', '.js', '.css']:
        # Match strings, single-line comments, and multi-line comments
        pattern = r'(".*?"|\'.*?\')|(/\*.*?\*/|//[^\r\n]*)'
        regex = re.compile(pattern, re.DOTALL)
        def replacer(match):
            if match.group(1) is not None:
                return match.group(1)
            else:
                return ""
        content = regex.sub(replacer, content)

    elif ext == '.html':
        # Remove HTML comments
        content = re.sub(r'<!--.*?-->', '', content, flags=re.DOTALL)
        # We also have inline scripts in HTML which use // or /* */
        # But a simple regex on the whole HTML file might break things if not careful.
        # Let's run the JS/CSS comment remover on the HTML content too, 
        # it usually handles <script> blocks reasonably well if strings are respected.
        pattern = r'(".*?"|\'.*?\')|(/\*.*?\*/|//[^\r\n]*)'
        regex = re.compile(pattern, re.DOTALL)
        def replacer(match):
            if match.group(1) is not None:
                return match.group(1)
            else:
                # Be careful not to remove URL schemes like http:// inside html tags!
                # Wait, inside HTML tags, URLs are inside strings like href="http://..."
                # So they will be matched by the string group!
                return ""
        content = regex.sub(replacer, content)
    
    if content != original_content:
        # Remove multiple blank lines resulting from comment deletion
        content = re.sub(r'\n\s*\n', '\n\n', content)
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"Cleaned {file_path}")

def process_directory(directory):
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.endswith(('.java', '.kt', '.js', '.html', '.css')):
                process_directory_path = os.path.join(root, file)
                remove_comments(process_directory_path)

if __name__ == "__main__":
    process_directory('digae_backend')
    process_directory('digae_android')
    process_directory('digae_web_admin')

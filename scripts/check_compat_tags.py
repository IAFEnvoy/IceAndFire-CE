"""Validate 1.20.1's one-way compatibility-tag boundary in sources or release JARs."""
import argparse
import json
from pathlib import Path
import zipfile


def validate(resources, label):
    graph = {}
    count = 0
    for name, raw in resources.items():
        parts = name.split('/')
        if len(parts) < 4 or parts[0] != 'data' or not name.endswith('.json'):
            continue
        namespace = parts[1]
        obj = json.loads(raw)
        if parts[2] != 'tags':
            if namespace == 'iceandfire':
                def check(value):
                    if isinstance(value, dict):
                        for key, entry in value.items():
                            if key == 'tag' and isinstance(entry, str):
                                assert not entry.startswith(('c:', 'forge:')), (label, name, entry)
                            check(entry)
                    elif isinstance(value, list):
                        for entry in value:
                            check(entry)
                check(obj)
            continue
        kind_length = 2 if parts[3] == 'worldgen' else 1
        kind = '/'.join(parts[3:3 + kind_length])
        path = '/'.join(parts[3 + kind_length:])[:-5]
        tag = (kind, namespace + ':' + path)
        imports = []
        count += 1
        for value in obj.get('values', []):
            identifier = value if isinstance(value, str) else value['id']
            if namespace in {'c', 'forge'}:
                assert not identifier.startswith('#'), (label, name, 'Foreign tags must be concrete exports', identifier)
                assert identifier.startswith('iceandfire:'), (label, name, 'Vanilla defaults belong in compat', identifier)
            if identifier.startswith('#'):
                target = identifier[1:]
                imports.append((kind, target))
                if target.startswith(('c:', 'forge:')):
                    assert namespace == 'iceandfire' and path.startswith('compat/'), (label, name, target)
                    assert isinstance(value, dict) and value.get('required') is False, (label, name, 'External import must be optional', value)
        graph[tag] = imports
    active, visited = set(), set()
    def visit(tag, chain):
        assert tag not in active, (label, 'Tag dependency cycle', chain + [tag])
        if tag[1].startswith('iceandfire:'):
            assert tag in graph, (label, 'Missing mod-owned tag', tag)
        if tag in visited:
            return
        active.add(tag)
        for target in graph.get(tag, []):
            visit(target, chain + [tag])
        active.remove(tag)
        visited.add(tag)
    for tag in graph:
        visit(tag, [])
    print(f'{label}: {count} tag definitions checked; no dependency cycles or reverse foreign bridges')


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('paths', nargs='*', type=Path)
    args = parser.parse_args()
    paths = args.paths or [Path(__file__).resolve().parents[1] / 'common/src/main/resources']
    for path in paths:
        if path.is_file():
            with zipfile.ZipFile(path) as jar:
                resources = {n: jar.read(n) for n in jar.namelist() if n.startswith('data/') and n.endswith('.json')}
        else:
            resources = {p.relative_to(path).as_posix(): p.read_bytes() for p in (path / 'data').rglob('*.json')}
        assert resources, ('No resources found', path)
        validate(resources, str(path))


if __name__ == '__main__':
    main()

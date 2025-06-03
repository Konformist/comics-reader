import { resolveUrl } from '@/core/utils/urlUtils.ts';
import { describe, expect, test } from 'vitest';

describe('urlUtils', () => {
  test('check localhost', () => {
    let result = resolveUrl('https://localhost/test/path?page=1', 'https://my.domain.ru');
    expect(result).toBe('https://my.domain.ru/test/path?page=1');

    result = resolveUrl('http://localhost/test/path?page=1', 'https://my.domain.ru');
    expect(result).toBe('https://my.domain.ru/test/path?page=1');

    result = resolveUrl('localhost/test/path?page=1', 'https://my.domain.ru');
    expect(result).toBe('https://my.domain.ru/test/path?page=1');
  });

  test('check absolute', () => {
    const result = resolveUrl('/test/path?page=1', 'https://my.domain.ru/test2');
    expect(result).toBe('https://my.domain.ru/test/path?page=1');
  });

  test('check relative', () => {
    const result = resolveUrl('test/path?page=1', 'https://my.domain.ru/test2');
    expect(result).toBe('https://my.domain.ru/test/path?page=1');
  });

  test('check domain', () => {
    const result = resolveUrl('http://test.domain.ru/test/path?page=1', 'https://my.domain.ru/test2');
    expect(result).toBe('http://test.domain.ru/test/path?page=1');
  });

  test('check multi slash', () => {
    let result = resolveUrl('/test//path?page=1', 'https://my.domain.ru/test2');
    expect(result).toBe('https://my.domain.ru/test/path?page=1');

    result = resolveUrl('/test///path?page=1', 'https://my.domain.ru/test2');
    expect(result).toBe('https://my.domain.ru/test/path?page=1');
  });
});

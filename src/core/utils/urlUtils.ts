export const getDomain = (v: string) => new URL(v).origin;

/**
 * Объединяет сырой путь с базовым URL, исключая localhost в качестве валидного домена.
 *
 * @param {string} rawPath - Относительный путь или потенциальный абсолютный URL.
 * @param {string} baseUrl - Базовый URL, откуда берётся только домен и протокол.
 * @returns {string|null} Финальный абсолютный URL или null в случае ошибки.
 */
export const resolveUrl = (rawPath: string, baseUrl: string): string | null => {
  try {
    const cleanedPath = rawPath.replace(/^(?:https?:\/\/)?(?:localhost|127\.0\.0\.1)/, '');
    const baseOrigin = getDomain(baseUrl);
    const fullUrl = new URL(cleanedPath, baseOrigin);
    return `${fullUrl.origin}${fullUrl.pathname.replaceAll(/\/{2,}/g, '/')}${fullUrl.search}${fullUrl.hash}`;
  } catch (e) {
    console.error('Invalid URL inputs:', e);
    return null;
  }
};

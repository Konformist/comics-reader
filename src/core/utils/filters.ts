export const filterString = (v: string, s: string) => (
  v.toLowerCase().includes(s.toLowerCase())
);

export const filterArrays = <T>(f: T[], s: T[]): boolean => (
  s.length === 0
  || f.some((e) => s.includes(e))
);

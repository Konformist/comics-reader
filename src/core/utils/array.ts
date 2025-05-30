export const sortString = (a: string, b: string) => {
  const f = a.toLowerCase();
  const s = b.toLowerCase();

  if (f > s) {
    return 1;
  } else if (f < s) {
    return -1;
  } else {
    return 0;
  }
};

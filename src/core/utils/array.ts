export const sortString = (a: string, b: string, reverse = false) => {
  const f = (reverse ? b : a).toLowerCase();
  const s = (reverse ? a : b).toLowerCase();

  if (f > s) {
    return 1;
  } else if (f < s) {
    return -1;
  } else {
    return 0;
  }
};

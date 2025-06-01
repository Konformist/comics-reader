export const getDomain = (v: string) => {
  const domain = v.match(/^(?:https?:\/\/)?(?:[^@\n]+@)?(?:www\.)?[^:/\n?]+/img);
  return domain ? domain[0] : '';
};

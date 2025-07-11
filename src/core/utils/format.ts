export const formatBytes = (bytes: number, decimals = 2) => {
  if (!+bytes) {
    return '0 Bytes';
  }

  const k = 1024;
  const dm = Math.max(decimals, 0);
  const sizes = ['Bytes', 'KiB', 'MiB', 'GiB', 'TiB', 'PiB', 'EiB', 'ZiB', 'YiB'];

  const i = Math.floor(Math.log(bytes) / Math.log(k));

  return `${Number.parseFloat((bytes / Math.pow(k, i)).toFixed(dm))} ${sizes[i]}`;
};

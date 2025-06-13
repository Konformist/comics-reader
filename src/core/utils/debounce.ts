// eslint-disable-next-line @typescript-eslint/no-unsafe-function-type
export default (fn: Function, delay: number) => {
  // Переменная для хранения таймаута
  // eslint-disable-next-line no-undef
  let debounceTimeout: NodeJS.Timeout | null = null;

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  return (...args: any[]) => {
    if (debounceTimeout) {
      clearTimeout(debounceTimeout); // Если уже есть таймаут, отменяем его
    }
    debounceTimeout = setTimeout(() => {
      fn(...args); // Вызываем функцию с аргументами
    }, delay);
  };
};

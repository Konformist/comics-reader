export default abstract class Entity<T> {
  abstract getDTO(): T;
}
